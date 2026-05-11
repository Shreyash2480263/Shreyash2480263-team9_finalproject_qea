Feature:Sign In with invalid credentials

  Scenario:Sign in with invalid mobile number shows an error
    Given the user is on the district.in home page
    When the user clicks the Sign In button
    And the user enters an invalid mobile number
    And the user clicks Continue
    Then an error message should be displayed
