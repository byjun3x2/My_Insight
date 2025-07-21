package com.myinsight.service;

import com.myinsight.entity.Post;
import com.myinsight.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Value("${recommendation.server.url}")
    private String recommendationUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Optional<Post> getPostById(Long id) {
        return postRepository.findById(id);
    }

    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    public Post updatePost(Long id, Post updatedPost) {
        Optional<Post> existingPost = postRepository.findById(id);
        if (existingPost.isPresent()) {
            Post post = existingPost.get();
            post.setTitle(updatedPost.getTitle());
            post.setContent(updatedPost.getContent());
            post.setTags(updatedPost.getTags());
            return postRepository.save(post);
        }
        return null;
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    public List<String> getRecommendations(String currentPostContent, List<String> comparePosts) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("currentPost", currentPostContent);
            request.put("comparePosts", comparePosts);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.exchange(recommendationUrl, HttpMethod.POST, entity, Map.class);

            List<Map> recommendations = (List<Map>) response.getBody().get("recommendations");
            return recommendations.stream()
                    .filter(rec -> (double) rec.get("similarity") > 0.2)
                    .map(rec -> (String) rec.get("post"))
                    .limit(6)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Python 추천 서버 호출 실패: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> getAllPostContents() {
        return getAllPosts().stream()
                .map(Post::getContent)
                .collect(Collectors.toList());
    }
}