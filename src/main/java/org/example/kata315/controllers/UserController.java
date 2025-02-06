package org.example.kata315.controllers;

import org.example.kata315.models.User;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final RestTemplate restTemplate;

    public UserController(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public StringBuilder getUsers() {
        String url = "http://94.198.50.185:7081/api/users";
        ResponseEntity<List<User>> getResponse = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<User>>() {}
        );
        String sessionId = getResponse
                .getHeaders()
                .get("set-cookie")
                .get(0)
                .substring(11, 43);

        User createdUser = new User();
        createdUser.setId(3L);
        createdUser.setName("James");
        createdUser.setLastName("Brown");
        createdUser.setAge((byte) 34);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", "JSESSIONID=" + sessionId); // Include the session ID in the headers
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<User> requestEntity = new HttpEntity<>(createdUser, headers);



        ResponseEntity<String> postResponse = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        StringBuilder code = new StringBuilder(postResponse.getBody());

        createdUser.setName("Thomas");
        createdUser.setLastName("Shelby");

        requestEntity = new HttpEntity<>(createdUser, headers);

        ResponseEntity<String> putResponse = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                requestEntity,
                String.class
        );

        code.append(putResponse.getBody());

        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                url + "/" + createdUser.getId(),
                HttpMethod.DELETE,
                requestEntity,
                String.class
        );
        code.append(deleteResponse.getBody());

        return code;
    }
}
