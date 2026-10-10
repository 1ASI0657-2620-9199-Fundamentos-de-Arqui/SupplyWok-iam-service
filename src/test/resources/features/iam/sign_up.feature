Feature: User Sign-Up
  As a new user of the SupplyWok platform
  I want to register an account with a specific business role
  So that I can access the microservices corresponding to my role

  Scenario: Successful sign-up as a restaurant user
    Given the email "owner@wokhouse.pe" is available for registration
    When the user signs up with email "owner@wokhouse.pe", password "Password123!" and role "RESTAURANT"
    Then the registration succeeds
    And the created user has email "owner@wokhouse.pe" and role "RESTAURANT"

  Scenario: Successful sign-up as a supplier user
    Given the email "contact@andesfresh.pe" is available for registration
    When the user signs up with email "contact@andesfresh.pe", password "Password123!" and role "SUPPLIER"
    Then the registration succeeds
    And the created user has email "contact@andesfresh.pe" and role "SUPPLIER"

  Scenario: Sign-up as admin is rejected
    Given any registration request
    When the user signs up with email "admin@supplywok.pe", password "SuperSecret!" and role "ADMIN"
    Then the registration fails with validation error "Role not allowed for sign-up"

  Scenario: Sign-up with duplicate email is rejected
    Given a user already exists with email "existing@wokhouse.pe"
    When the user signs up with email "existing@wokhouse.pe", password "NewPassword123!" and role "RESTAURANT"
    Then the registration fails with a conflict error
