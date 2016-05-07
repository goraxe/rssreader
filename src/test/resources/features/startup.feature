Feature: basic statup test
  Scenario: client makes a call to /
    When the client calls /
    Then the client recieves status code 200
