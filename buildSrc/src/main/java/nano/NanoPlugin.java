package nano;

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;

public class NanoPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register(
                "stage",
                Exec.class,
                (it) -> it.commandLine("bash", "-c", "./gradlew build -x test")
        );

        project.subprojects((it) -> it.getExtensions().getExtraProperties().set("javaVersion", JavaVersion.VERSION_17));
    }
}