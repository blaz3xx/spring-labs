package com.example.lab6.services;

import com.example.lab6.dto.PostRequest;
import com.example.lab6.dto.PostResponse;
import com.example.lab6.exceptions.NotFoundException;
import com.example.lab6.models.Post;
import com.example.lab6.models.Topic;
import com.example.lab6.repositories.PostRepository;
import com.example.lab6.repositories.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    public PostServiceImpl(PostRepository postRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.topicRepository = topicRepository;
    }

    public List<PostResponse> findByTopic(Long topicId) {
        getTopic(topicId);
        return postRepository.findByTopic_IdOrderByIdAsc(topicId).stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse findById(Long topicId, Long postId) {
        return PostResponse.from(getPost(topicId, postId));
    }

    @Transactional
    public PostResponse create(Long topicId, PostRequest request) {
        Topic topic = getTopic(topicId);
        Post post = new Post(request.content().strip(), request.author().strip());
        topic.addPost(post);
        return PostResponse.from(postRepository.save(post));
    }

    @Transactional
    public void delete(Long topicId, Long postId) {
        Post post = getPost(topicId, postId);
        post.getTopic().getPosts().remove(post);
        postRepository.delete(post);
    }

    public List<PostResponse> search(String text) {
        return postRepository.searchByContent(text.strip()).stream()
                .map(PostResponse::from)
                .toList();
    }

    public List<PostResponse> findByAuthor(String author) {
        return postRepository.findByAuthorIgnoreCaseOrderByCreationDateDesc(author.strip()).stream()
                .map(PostResponse::from)
                .toList();
    }

    private Post getPost(Long topicId, Long postId) {
        getTopic(topicId);
        return postRepository.findByIdAndTopic_Id(postId, topicId)
                .orElseThrow(() -> new NotFoundException("Допис з id=" + postId + " у темі з id=" + topicId + " не знайдено"));
    }

    private Topic getTopic(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new NotFoundException("Тему з id=" + topicId + " не знайдено"));
    }
}
