Feature: Native image Gherkin

  @id:scn~native-image-gherkin~1
  Scenario: Built in Gherkin importer is available
    Given a native executable
    When the executable traces this feature
    Then the scenario is imported
