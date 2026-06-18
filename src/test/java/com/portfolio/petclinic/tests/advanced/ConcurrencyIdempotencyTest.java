package com.portfolio.petclinic.tests.advanced;

import com.portfolio.petclinic.base.BaseTest;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.utils.ResponseValidator;
import com.portfolio.petclinic.utils.TestDataFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Epic("Spring Petclinic API")
@Feature("Advanced QA Techniques")
class ConcurrencyIdempotencyTest extends BaseTest {

    @Test
    @Story("Concurrent create")
    @DisplayName("Sequential owner creation should produce distinct owner IDs")
    void sequentialOwnerCreateShouldProduceDistinctOwnerIds() {
        var first = ownersClient.createOwner(TestDataFactory.buildOwner());
        var second = ownersClient.createOwner(TestDataFactory.buildOwner());

        ResponseValidator.assertStatusCode(first.getStatusCode(), 201);
        ResponseValidator.assertStatusCode(second.getStatusCode(), 201);

        Set<Integer> ids = new HashSet<>();
        ids.add(first.as(Owner.class).getId());
        ids.add(second.as(Owner.class).getId());
        assertThat(ids.size(), is(2));

        ids.forEach(id -> ownersClient.deleteOwner(id));
    }

    @Test
    @Story("Duplicate create")
    @DisplayName("Repeated owner POST requests should create independent resources")
    void repeatedOwnerCreateShouldCreateIndependentResources() {
        Owner payload = TestDataFactory.buildOwner();

        Response first = ownersClient.createOwner(payload);
        Response second = ownersClient.createOwner(payload);

        ResponseValidator.assertStatusCode(first.getStatusCode(), 201);
        ResponseValidator.assertStatusCode(second.getStatusCode(), 201);
        assertThat(first.as(Owner.class).getId().equals(second.as(Owner.class).getId()), is(false));

        ownersClient.deleteOwner(first.as(Owner.class).getId());
        ownersClient.deleteOwner(second.as(Owner.class).getId());
    }
}
