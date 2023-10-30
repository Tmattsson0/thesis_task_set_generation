Feature: save csv file
  @saveCsv
  Scenario: Saving a file in the correct subfolder
    Given an empty testCasesTest directory
    When the csv method is called
    Then a file with the correct filename is created in a subfolder matching parameter name






#    Given an example scenario
#    When all step definitions are implemented
#    Then the scenario passes