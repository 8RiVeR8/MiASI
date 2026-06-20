package com.project.youtlix.architecture;

import com.project.youtlix.architecture.rules.ProductionCodeRules;
import com.project.youtlix.testsupport.annotation.ArchitectureTest;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@ArchitectureTest
@AnalyzeClasses(packages = "com.project.youtlix", importOptions = com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests.class)
class ArchitectureRulesTest {

    @ArchTest
    static final ArchRule productionCodeMustNotDependOnJUnit = ProductionCodeRules.productionCodeMustNotDependOnJUnit();
}
