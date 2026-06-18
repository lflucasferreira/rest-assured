package com.portfolio.petclinic.utils;

import com.github.javafaker.Faker;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;

import java.time.LocalDate;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public final class TestDataFactory {

    private static final Faker FAKER = new Faker(Locale.ENGLISH);

    private TestDataFactory() {
    }

    public static Owner buildOwner() {
        Owner owner = new Owner();
        owner.setFirstName(sanitizeName(FAKER.name().firstName()));
        owner.setLastName(sanitizeName(FAKER.name().lastName()));
        owner.setAddress(FAKER.address().streetAddress());
        owner.setCity(FAKER.address().city());
        owner.setTelephone(generateTelephone());
        return owner;
    }

    public static PetFields buildPetFields(int petTypeId, String petTypeName) {
        PetFields petFields = new PetFields();
        petFields.setName(sanitizePetName(FAKER.animal().name()));
        petFields.setBirthDate(randomBirthDate());
        petFields.setType(new PetType(petTypeId, petTypeName));
        return petFields;
    }

    public static Pet buildPetUpdatePayload(Pet existingPet, String updatedName) {
        Pet pet = new Pet();
        pet.setId(existingPet.getId());
        pet.setName(updatedName);
        pet.setBirthDate(existingPet.getBirthDate());
        pet.setType(existingPet.getType());
        pet.setOwnerId(existingPet.getOwnerId());
        return pet;
    }

    private static String sanitizeName(String value) {
        return value.replaceAll("[^\\p{L} '-]", "").trim();
    }

    private static String sanitizePetName(String value) {
        String sanitized = value.replaceAll("[^\\p{L}0-9 '-]", "").trim();
        return sanitized.isEmpty() ? "Buddy" : sanitized.substring(0, Math.min(sanitized.length(), 20));
    }

    private static String generateTelephone() {
        return String.format("%010d", ThreadLocalRandom.current().nextLong(1_000_000_000L, 9_999_999_999L));
    }

    private static LocalDate randomBirthDate() {
        int year = ThreadLocalRandom.current().nextInt(2015, LocalDate.now().getYear());
        int month = ThreadLocalRandom.current().nextInt(1, 13);
        int day = ThreadLocalRandom.current().nextInt(1, 28);
        return LocalDate.of(year, month, day);
    }
}
