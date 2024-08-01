package org.example.service;

import org.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class UserService {


    private final RestTemplate restTemplate;
    private final HttpHeaders headers;
    private final String URL = "http://94.198.50.185:7081/api/users";


    /**В строке this.headers.set("Cookie",String.join(";", Objects.requireNonNull(restTemplate.headForHeaders(URL).get("Set-Cookie"))));
     *происходит установка значения заголовка "Cookie" в объекте headers.
     *Для этого используется метод set() класса HttpHeaders, который позволяет установить
     *определенное значение для указанного заголовка. Значение для заголовка "Cookie" формируется
     *с помощью вызова метода String.join(), который объединяет несколько строк в одну,
     * разделяя их символом точка с запятой.

     * Параметр "Set-Cookie" получается путем вызова метода headForHeaders() объекта restTemplate
     *с передачей ему URL в качестве аргумента. Метод headForHeaders() используется для выполнения HTTP HEAD
     *запроса к указанному URL и возвращения заголовков ответа в виде объекта HttpHeaders.
     *После этого извлекается значение заголовка "Set-Cookie" и устанавливается в качестве значения
     * заголовка "Cookie" в объекте headers.

     * Таким образом, эта строка кода позволяет установить значение заголовка "Cookie" в объекте headers
     *на основе полученного значения "Set-Cookie" в ответе от сервера при выполнении HEAD запроса
     *по указанному URL с использованием объекта restTemplate.**/
    @Autowired
    public UserService(RestTemplate restTemplate, HttpHeaders headers) {
        this.restTemplate = restTemplate;
        this.headers = headers;

        this.headers.set("Cookie",
                String.join(";", Objects.requireNonNull(restTemplate.headForHeaders(URL).get("Set-Cookie"))));
    }

    public String getAnswer() {
        return addUser().getBody() + updateUser().getBody() + deleteUser().getBody();
    }

    // Получение всех пользователей -  …/api/users ( GET )
    /**
     1. restTemplate.exchange(URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() { }):
     В этой строке кода метод exchange объекта restTemplate выполняет HTTP запрос GET по указанному URL
     и ожидает ответ в виде списка объектов типа User. Поскольку тип List<User> — параметризованный тип,
     используется ParameterizedTypeReference для указания правильного типа во время выполнения.

     2. ResponseEntity<List<User>> responseEntity = ...: Результат запроса сохраняется в объекте ResponseEntity,
     который содержит как данные ответа, так и другую информацию, включая заголовки ответа.

     3. System.out.println(responseEntity.getHeaders()): В этой строке кода выводятся заголовки ответа на консоль.
     Это может быть полезным для отладки или проверки содержимого ответа.

     4. return responseEntity.getBody(): Метод getBody() объекта ResponseEntity используется для получения тела ответа,
     то есть фактических данных о пользователях, которые были получены в ответ на запрос. Он возвращает список
     пользователей, извлеченный из тела ответа.

     Таким образом, метод getAllUsers() выполняет HTTP GET запрос к указанному URL, извлекает список пользователей
     из ответа, выводит заголовки ответа на консоль и возвращает список пользователей в виде списка объектов типа User.
     */
    private List<User> getAllUsers() {
        ResponseEntity<List<User>> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() { });
        System.out.println(responseEntity.getHeaders());
        return responseEntity.getBody();
    }

    // Добавление пользователя - …/api/users ( POST )
    /**
     1. User user = new User(3L, "James", "Brown", (byte) 5);: В этой строке создается новый объект типа User
     с определенными полями: id=3L, firstName="James", lastName="Brown", age=5.

     2. HttpEntity<User> entity = new HttpEntity<>(user, headers);: Здесь создается объект HttpEntity с телом запроса,
     содержащим нового пользователя (user) и заголовками (headers). Данные пользователя будут отправлены
     на сервер в этом объекте.

     3. return restTemplate.postForEntity(URL, entity, String.class);: В этой строке кода метод postForEntity()
     объекта restTemplate отправляет POST запрос на указанный URL, отправляя объект entity как тело запроса.
     Метод ожидает ответ в виде строки (String.class) и возвращает объект ResponseEntity<String>,
     содержащий информацию о выполненном запросе, включая содержимое тела ответа.

     Таким образом, метод addUser() отправляет POST запрос на сервер, включая информацию о новом пользователе,
     и возвращает ResponseEntity<String>, который содержит результат выполнения запроса, включая возможные данные,
     возвращенные сервером в ответ.
     */
    private ResponseEntity<String> addUser() {
        User user = new User(3L, "James", "Brown", (byte) 28);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return restTemplate.postForEntity(URL, entity, String.class);
    }

    // Изменение пользователя - …/api/users ( PUT )
    private ResponseEntity<String> updateUser() {
        User user = new User(3L, "Thomas", "Shelby", (byte) 5);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return restTemplate.exchange(URL, HttpMethod.PUT, entity, String.class, 3);
    }

    /**
     1. Map<String, Long> uriVariables = new HashMap<>() {{ put("id", 3L); }};: В этой строке создается объект uriVariables
     типа Map, содержащий параметры пути для URL. В данном случае устанавливается значение с ключом "id" равным 3L.
     Это позволит подставить значение идентификатора пользователя в URL запроса.

     2. HttpEntity<String> entity = new HttpEntity<>(null, headers);: Здесь создается объект HttpEntity с пустым
     телом запроса (null) и заголовками (headers). Для операции DELETE не требуется передавать тело запроса,
     поэтому используется пустой объект.

     3. return restTemplate.exchange(URL + "/{id}", HttpMethod.DELETE, entity, String.class, uriVariables);:
     В этой строке метод exchange объекта restTemplate выполняет DELETE запрос на указанный URL с использованием параметров пути.
     Метод ожидает ответ в виде строки (String.class). Параметр uriVariables указывает restTemplate как подставить
     значение id в URL.

     Таким образом, метод deleteUser() отправляет DELETE запрос на сервер для удаления пользователя с идентификатором 3, и возвращает ResponseEntity<String>, который содержит информацию о выполненном запросе, включая содержимое тела ответа
     * */
    // Удаление пользователя - …/api/users /{id} ( DELETE )
    private ResponseEntity<String> deleteUser() {
        Map<String, Long> uriVariables = new HashMap<>() {{
            put("id", 3L);
        }};
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(URL + "/{id}", HttpMethod.DELETE, entity, String.class, uriVariables);
    }
}
