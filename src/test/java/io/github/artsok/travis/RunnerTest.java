package io.github.artsok.travis;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/yandexmarket/features",
        glue = "io.github.artsok.travis.steps",
        tags = "@All" // @All, @1, @2

)
public class RunnerTest {

}