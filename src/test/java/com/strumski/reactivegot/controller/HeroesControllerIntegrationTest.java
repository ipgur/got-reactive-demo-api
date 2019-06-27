/*
 * Copyright 2019 igur.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.strumski.reactivegot.controller;

import com.strumski.reactivegot.dao.HeroesRepository;
import com.strumski.reactivegot.entities.Hero;
import com.strumski.reactivegot.entities.House;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class HeroesControllerIntegrationTest {

    List<House> houses = Arrays.asList(
        new House("Starks"),
        new House("Targaryens")
    );

    List<Hero> heroes = Arrays.asList(
            new Hero("Jon Snow", houses.get(0)),
            new Hero("Ned Stark", houses.get(0)),
            new Hero("Robb Stark", houses.get(0)),
            new Hero("Daenerys Targaryen", houses.get(1))
    );

    @Autowired
    private HeroesRepository dao;

    @Autowired
    private WebTestClient client;

    @Before
    public void setUp() {
        dao.deleteAll()
                .thenMany(Flux.fromIterable(heroes))
                .flatMap(dao::save)
                .then()
                .doOnEach(System.out::println)
                .block();
    }

    @Test
    public void testFindAll() {
        client.get().uri("/heroes")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Hero.class)
                .hasSize(4)
                .consumeWith(System.out::println);
    }

    @Test
    public void testFindById() {
        client.get().uri("/heroes/{id}", heroes.get(0).getName())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Hero.class)
                .hasSize(1)
                .consumeWith(System.out::println);
    }

    @Test
    public void testCreateHero() {
        Hero aryaStark = new Hero("Arya Stark", houses.get(0));

        client.post().uri("/heroes")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(aryaStark), Hero.class)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.name").isNotEmpty()
                .jsonPath("$.house").isNotEmpty()
                .consumeWith(System.out::println);
    }

    @Test
    public void testFindAllByHouse() {
        client.get().uri("/heroes/house/{id}", houses.get(0).getName())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Hero.class)
                .hasSize(3)
                .consumeWith(System.out::println);
    }

    @Test
    public void testDeleteAll() {
        testCountQuick(4);

        client.delete().uri("/heroes")
                .exchange()
                .expectStatus().is2xxSuccessful();

        testCountQuick(0);
    }

    @Test
    public void testDeleteHero() {
        testCountQuick(4);

        client.delete().uri("/heroes/Robb Stark")
                .exchange()
                .expectStatus().is2xxSuccessful();

        testCountQuick(3);
    }

    private void testCountQuick(int count) {
        client.get().uri("/heroes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Hero.class)
                .hasSize(count);
    }
}
