Feature: task set generation

  Scenario: generating a task set with a given tt or et utilization
    Given a utilization of 0.7 and a list of periods
    When a task set is generated
    Then the utilization of the task set will be 0.7