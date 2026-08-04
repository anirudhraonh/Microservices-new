package com.prgrammingtechie.demo.repo;

import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends org.springframework.data.mongodb.repository.MongoRepository<com.prgrammingtechie.demo.model.Product, String> {

}
