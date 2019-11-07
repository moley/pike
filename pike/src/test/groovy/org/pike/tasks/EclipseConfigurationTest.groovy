package org.pike.tasks

import org.junit.Test
import org.pike.configuration.Configuration

class EclipseConfigurationTest {

    @Test(expected = IllegalStateException)
    public void overlappingDifferentInSameFile () {
        Configuration configuration1 = new Configuration()
        configuration1.encoding = "ISO-8859-15"

        Configuration configuration2 = new Configuration()
        configuration2.encoding = "UTF-8"

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void overlappingSameInSameFile () {
        Configuration configuration1 = new Configuration()
        configuration1.encoding = "ISO-8859-15"

        Configuration configuration2 = new Configuration()
        configuration2.encoding = "ISO-8859-15"

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void nonOverlappingCheckBoolean () {
        Configuration configuration1 = new Configuration()
        configuration1.showMemory true

        Configuration configuration2 = new Configuration()

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }

    @Test
    public void overlappingCheckBoolean () {
        Configuration configuration1 = new Configuration()
        configuration1.showMemory true

        Configuration configuration2 = new Configuration()
        configuration2.showMemory true

        EclipseConfiguration eclipseConfiguration = new EclipseConfiguration()
        eclipseConfiguration.check(Arrays.asList(configuration1, configuration2))
    }
}