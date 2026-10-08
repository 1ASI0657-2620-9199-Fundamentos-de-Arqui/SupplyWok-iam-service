Feature: Profile Management
  As an authenticated user
  I want to update my profile information
  So that my organization details are accurate and other users' profiles remain untouched

  Scenario: User updating a profile does not change another user's profile
    Given an existing profile for "first@restaurant.pe" with business name "First Restaurant"
    And an existing profile for "second@restaurant.pe" with business name "Second Restaurant"
    When "first@restaurant.pe" updates their business name to "First Restaurant Updated"
    Then the profile of "first@restaurant.pe" has business name "First Restaurant Updated"
    And the profile of "second@restaurant.pe" still has business name "Second Restaurant"

  Scenario: User successfully updates profile contact details
    Given an existing profile for "owner@wokhouse.pe" with business name "Wok House"
    When "owner@wokhouse.pe" updates their contact details to city "Lima" and phone "+51999888777"
    Then the profile of "owner@wokhouse.pe" reflects city "Lima" and support contact "+51999888777"

  Scenario: Updating a supplier profile triggers supplier profile sync event
    Given an existing profile for "distribuidora@fresh.pe" of type "SUPPLIER"
    When "distribuidora@fresh.pe" updates their business name to "Distribuidora Fresh Andes SAC"
    Then the profile of "distribuidora@fresh.pe" has business name "Distribuidora Fresh Andes SAC"
    And a supplier profile sync event is published
