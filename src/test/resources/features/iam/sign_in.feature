Feature: User Sign-In
  As a registered SupplyWok user
  I want to authenticate with my email and password
  So that I obtain a security token to access protected platform APIs

  Scenario: Successful sign-in with right password returns a token
    Given an existing user with email "carlos@wokhouse.pe", password "CorrectPassword123" and role "RESTAURANT"
    When the user signs in with email "carlos@wokhouse.pe" and password "CorrectPassword123"
    Then the sign-in succeeds
    And a valid access token is returned

  Scenario: Sign-in with wrong password fails
    Given an existing user with email "carlos@wokhouse.pe", password "CorrectPassword123" and role "RESTAURANT"
    When the user signs in with email "carlos@wokhouse.pe" and password "WrongPassword"
    Then the sign-in fails with invalid credentials error

  Scenario: Sign-in with non-existent email fails
    Given no user is registered with email "unknown@wokhouse.pe"
    When the user signs in with email "unknown@wokhouse.pe" and password "AnyPassword"
    Then the sign-in fails because the user was not found
