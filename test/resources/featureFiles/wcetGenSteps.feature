Feature: wcet generation

  Scenario: Generating a set of 10 wcets based on a set of 10 periods and desired utilization bounds
    Given a set of 10 periods, a desired utilization of 0.6 and individual task util bounds of 0.1 and 0.5
    When the wcet generator is used
    Then a set of wcets is produced that is within individual task util bounds of 0.1 and 0.6