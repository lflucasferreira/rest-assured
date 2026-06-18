package com.portfolio.petclinic.utils;

import com.github.javafaker.Faker;
import com.portfolio.petclinic.models.Owner;
import com.portfolio.petclinic.models.Pet;
import com.portfolio.petclinic.models.PetFields;
import com.portfolio.petclinic.models.PetType;
import com.portfolio.petclinic.models.Specialty;
import com.portfolio.petclinic.models.User;
import com.portfolio.petclinic.models.Vet;
import com.portfolio.petclinic.models.Visit;
import com.portfolio.petclinic.models.VisitFields;

import java.time.LocalDate;
import java.util.Locale;
import java.util.List;
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

    public static VisitFields buildVisitFields() {
        VisitFields visitFields = new VisitFields();
        visitFields.setDate(LocalDate.now().minusDays(1));
        visitFields.setDescription("Routine checkup - " + FAKER.lorem().word());
        return visitFields;
    }

    public static Owner buildInvalidOwnerWithAlphabeticTelephone() {
        Owner owner = buildOwner();
        owner.setTelephone("NOT-A-PHONE");
        return owner;
    }

    public static Owner buildEmptyOwner() {
        return new Owner();
    }

    public static Owner buildOwnerUpdatePayload(Owner existingOwner, String updatedLastName, String updatedCity) {
        Owner owner = new Owner();
        owner.setId(existingOwner.getId());
        owner.setFirstName(existingOwner.getFirstName());
        owner.setLastName(updatedLastName);
        owner.setAddress(existingOwner.getAddress());
        owner.setCity(updatedCity);
        owner.setTelephone(existingOwner.getTelephone());
        return owner;
    }

    public static PetFields buildPetFieldsWithFutureBirthDate(int petTypeId, String petTypeName) {
        PetFields petFields = buildPetFields(petTypeId, petTypeName);
        petFields.setBirthDate(LocalDate.now().plusYears(1));
        return petFields;
    }

    public static PetFields buildPetFieldsWithEmptyName(int petTypeId, String petTypeName) {
        PetFields petFields = buildPetFields(petTypeId, petTypeName);
        petFields.setName("");
        return petFields;
    }

    public static VisitFields buildInvalidVisitWithEmptyDescription() {
        VisitFields visitFields = new VisitFields();
        visitFields.setDate(LocalDate.now().minusDays(1));
        visitFields.setDescription("");
        return visitFields;
    }

    public static Visit buildVisitUpdatePayload(Visit existingVisit, String updatedDescription) {
        Visit visit = new Visit();
        visit.setId(existingVisit.getId());
        visit.setPetId(existingVisit.getPetId());
        visit.setDate(existingVisit.getDate());
        visit.setDescription(updatedDescription);
        return visit;
    }

    public static Specialty buildSpecialty(String name) {
        Specialty specialty = new Specialty();
        specialty.setName(name);
        return specialty;
    }

    public static Vet buildVet(String firstName, String lastName) {
        Vet vet = new Vet();
        vet.setFirstName(firstName);
        vet.setLastName(lastName);
        vet.setSpecialties(java.util.Collections.emptyList());
        return vet;
    }

    public static PetType buildPetType(String name) {
        return new PetType(null, name);
    }

    private static String uniqueSuffix() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(10000, 99999));
    }

    public static Specialty buildUniqueSpecialty() {
        return buildSpecialty("specialty-" + uniqueSuffix());
    }

    public static Vet buildUniqueVet() {
        return buildVet(sanitizeName(FAKER.name().firstName()), sanitizeName(FAKER.name().lastName()));
    }

    public static PetType buildUniquePetType() {
        return buildPetType("type-" + uniqueSuffix());
    }

    public static User buildUser(String username, String password, String roleName) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(true);
        user.setRoles(List.of(new User.RoleRef(roleName)));
        return user;
    }

    public static User buildUniqueSecureUser(String roleName) {
        String suffix = uniqueSuffix();
        return buildUser("user" + suffix, "pass" + suffix, roleName);
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
