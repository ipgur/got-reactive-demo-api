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
import com.strumski.reactivegot.exceptions.HouseNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@RestController
@RequestMapping("/heroes")
public class HeroesController {

    private HeroesRepository dao;

    @Autowired
    HeroesController(HeroesRepository dao) {
        this.dao = dao;
    }

    @GetMapping
    public Flux<Hero> getAllHeroes() {
        return dao.findAll();
    }

    @GetMapping("{id}")
    public Mono<Hero> getHero(@PathVariable String id) {
        return dao.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Hero> saveHero(@RequestBody Hero hero) {
        return dao.save(hero);
    }

    @GetMapping("/house/{houseName}")
    public Flux<Hero> getAllHeroesByHouse(@PathVariable String houseName) {
        House house = House.fromString(houseName);
        if (house == null) {
            throw new HouseNotFoundException();
        }
        return dao.findAllByHouse(house);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAllHeroes() {
        return dao.deleteAll();
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteHero(@PathVariable String id) {
        return dao.deleteById(id);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Hero>> updateHero(@PathVariable(value = "id") String id,
                                                 @RequestBody Hero hero) {

        return dao.findById(id)
                .flatMap(existingHero -> {
                    existingHero.setName(hero.getName());
                    existingHero.setHouse(hero.getHouse());
                    return dao.save(existingHero);
                })
                .map(updateHero -> new ResponseEntity<>(updateHero, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
