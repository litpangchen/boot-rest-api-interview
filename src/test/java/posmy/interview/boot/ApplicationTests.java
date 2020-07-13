package posmy.interview.boot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import posmy.interview.boot.constants.UserRole;
import posmy.interview.boot.model.Book;
import posmy.interview.boot.vo.ResponseResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = DEFINED_PORT)
class ApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Check Web App startup result")
    void contextLoads() {
    }

    /**
     * library system offers two endpoints
     * need to have http basic auth info only can access resource /users /books
     */
    @Test
    void testAccessRight() {
        ResponseEntity<String> response = restTemplate.getForEntity("/books", String.class);
        assertThat(response)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(UNAUTHORIZED);

        response = restTemplate.getForEntity("/users", String.class);
        assertThat(response)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(UNAUTHORIZED);
    }

    /**
     * when the web app , it will create dummy 2 dummy users , (password same as username) , and 2 dummy books
     * 1. librarian1
     * 2. member1
     * <p>
     * 1. book1
     * 2. book2
     */
    @Test
    void testReadUsersAndBookByLibrarian() {
        //
        ResponseEntity<ResponseResult> responseEntity = restTemplate
                .withBasicAuth("librarian1", "librarian1")
                .getForEntity("/books", ResponseResult.class);
        printJson(responseEntity);

        assertEquals(200, responseEntity.getBody().getCode());

        responseEntity =
                restTemplate.withBasicAuth("librarian1", "librarian1")
                        .getForEntity("/users", ResponseResult.class);
        assertEquals(200, responseEntity.getBody().getCode());

        printJson(responseEntity);
    }

    @Test
    void testMemberReadBook() {

        // read all books
        ResponseEntity<String> response = restTemplate.withBasicAuth("member1", "member1")
                .getForEntity("/books", String.class);
        printJson(response);
        assertThat(response)
                .extracting(ResponseEntity::getStatusCode)
                .isEqualTo(HttpStatus.OK);

        // read single book id = 1
        ResponseEntity<ResponseResult> responseResult = restTemplate.withBasicAuth("member1", "member1")
                .getForEntity("/books/1", ResponseResult.class);
        printJson(responseResult);
        assertEquals(200, responseResult.getBody().getCode());
    }

    @Test
    void testUpdateBookStatusByMember() throws JSONException {

        RestTemplate customRestTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(10000);

        customRestTemplate.setRequestFactory(requestFactory);

        //HttpHeader
        String base64Credential = new String(Base64.encodeBase64(("member1:member1").getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credential);
        HttpEntity<Object> requestEntity = new HttpEntity<Object>(headers);

        // member borrow book id 1
        ResponseEntity<ResponseResult> responseResult =
                customRestTemplate.exchange("http://localhost:8080/books/borrow/1", HttpMethod.PATCH, requestEntity, ResponseResult.class);

        // by default restTemplate not support patch method due to the internal factory implementation not support patch method
        //restTemplate.withBasicAuth("member1", "member1").patchForObject("/books/borrow/1",);
        printJson(responseResult);

        // check book id 1 is in borrowed status
        ResponseEntity<ResponseResult> responseResult1 =
                restTemplate.withBasicAuth("member1", "member1").getForEntity("/books/1", ResponseResult.class);
        printJson(responseResult);

        JSONObject jsonObject = new JSONObject(responseResult1.getBody().getData().toString());
        Integer bookStatus = jsonObject.getInt("bookStatus");
        assertEquals(Book.BookStatus.BORROWED, bookStatus);

        // member1 returned book
        responseResult =
                customRestTemplate.exchange("http://localhost:8080/books/return/1", HttpMethod.PATCH, requestEntity, ResponseResult.class);
        printJson(responseResult);

        responseResult1 =
                restTemplate.withBasicAuth("member1", "member1").getForEntity("/books/1", ResponseResult.class);
        jsonObject = new JSONObject(responseResult1.getBody().getData().toString());
        bookStatus = jsonObject.getInt("bookStatus");
        assertEquals(Book.BookStatus.AVAILABLE, bookStatus);
    }

    @Test
    void testCreateUpdateDeleteBookOnlyCanBePerformedByLibrarian() throws JSONException {

        // librarian create new book called book3

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(2);
        map.add("bookName", "book3");
        map.add("author", "author3");

        ResponseEntity<ResponseResult> responseEntity = restTemplate
                .withBasicAuth("librarian1", "librarian1")
                //.postForObject("/books",`)
                .postForEntity("/books", map, ResponseResult.class);
        printJson(responseEntity);

        assertEquals(200, responseEntity.getBody().getCode());

        // librarian check book3 is created
        responseEntity = restTemplate.withBasicAuth("librarian1", "librarian1")
                .getForEntity("/books/" + 3, ResponseResult.class);
        printJson(responseEntity);

        ResponseResult<?> responseResult = responseEntity.getBody();
        JSONObject jsonObject = new JSONObject(responseResult.getData().toString());
//        Book book = (Book) responseResult.getData();
        assertEquals("book3", jsonObject.getString("bookName")); // check book is book3

        // librarian update book id 3 , try update author3 -> author33
        map = new LinkedMultiValueMap<String, String>();
        map.add("author", "author33");

        restTemplate.withBasicAuth("librarian1", "librarian1")
                .put("/books/" + 3, map, ResponseResult.class);

        // librarian get book id 3 , to check the changes result
        responseEntity = restTemplate.withBasicAuth("librarian1", "librarian1")
                .getForEntity("/books/" + 3, ResponseResult.class);
        printJson(responseEntity);

        responseResult = responseEntity.getBody();
        jsonObject = new JSONObject(responseResult.getData().toString());
        assertEquals("author33", jsonObject.getString("author")); // check book author is author33

        // librarian delete book id 3
        restTemplate
                .withBasicAuth("librarian1", "librarian1")
                .delete("/books/" + 3);

        // librarian check book id 3 is still exists
        responseEntity = restTemplate.withBasicAuth("librarian1", "librarian1")
                .getForEntity("/books/" + 3, ResponseResult.class);
        printJson(responseEntity);
        responseResult = responseEntity.getBody();
        assertEquals(404, responseResult.getCode());
    }

    @Test
    void testAllOperationOnUserByLibrarian() throws JSONException {

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(2);
        map.add("username", "librarian2");
        map.add("userRoleId", String.valueOf(UserRole.LIBRARIAN.getId()));

        // create new user called librarian2
        ResponseEntity<ResponseResult> responseResult =
                restTemplate.withBasicAuth("librarian1", "librarian1")
                        .postForEntity("/users", map, ResponseResult.class);

        printJson(responseResult);
        assertEquals(200, responseResult.getBody().getCode());

        // check librarian2 already created

        // read all users and check librarian2 is exists
        responseResult = restTemplate.withBasicAuth("librarian1", "librarian1")
                .getForEntity("/users", ResponseResult.class);
        printJson(responseResult);
        JSONArray jsonArray = new JSONArray(responseResult.getBody().getData().toString());

        boolean createdUser = false;
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("username").equals("librarian2")) {
                createdUser = true;
                break;
            }
        }
        assertTrue(createdUser);

        // delete all acc
        restTemplate.withBasicAuth("librarian1", "librarian1").delete("/users");

        // check still have member type acc
        responseResult = restTemplate.withBasicAuth("librarian1", "librarian1")
                .getForEntity("/users", ResponseResult.class);
        printJson(responseResult);
    }

    @Test
    void testMemberCanOnlyPerformReadAndDeleteOperation() throws JSONException {
        ResponseEntity<ResponseResult> responseResult = restTemplate.withBasicAuth("member1", "member1")
                .getForEntity("/users", ResponseResult.class);
        printJson(responseResult);
        String username = new JSONObject(responseResult.getBody().getData().toString()).getString("username");
        assertEquals("member1", username);

        // delete own acc
        restTemplate.withBasicAuth("member1", "member1").delete("/users/2");

        // check user id = 2 still exixsts
        responseResult = restTemplate.withBasicAuth("librarian1", "librarian1").getForEntity("/users/2", ResponseResult.class);
        printJson(responseResult);

        assertEquals(404, responseResult.getBody().getCode());
    }

    @Test
    void printJson(Object object) {
        try {
            String result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
