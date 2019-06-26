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
package com.strumski.reactivegot;

import com.strumski.reactivegot.dao.HeroesRepository;
import com.strumski.reactivegot.entities.Hero;
import com.strumski.reactivegot.entities.House;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class HeroesInit implements ApplicationRunner {
    private final static List<House> houses = Arrays.asList(
            new House("starks"),
            new House("targerian")
    );

    private final static List<Hero> heroes = Arrays.asList(
            new Hero("John Snow", houses.get(0)),
            new Hero("Deneris Snowborn", houses.get(1))
    );

    @Autowired
    private HeroesRepository dao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dao.deleteAll()
           .thenMany(Flux.fromIterable(heroes))
           .flatMap(dao::save)
           .thenMany(dao.findAll())
           .subscribe(System.out::println);
    }
}
