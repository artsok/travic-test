@All
Feature: Testing Yandex Market

  @1
  Scenario: First searching
    Given I open the browser and expand to full screen
    And I open yandex.ru
    When I select "Маркет"(market.yandex.ru)
    And I select the section "Компьютеры"
    And I select the subsection "Ноутбуки"
    And I go to advanced search
    And I set the search price parametr to 30000 rubles
    And I choose the manufacturers HP, Lenovo


  @2
  Scenario: Second searching
    Given I open the browser and expand to full screen
    And I open yandex.ru
    When I select "Маркет"(market.yandex.ru)
