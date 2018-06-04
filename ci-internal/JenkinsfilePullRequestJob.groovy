@Library('surf-lib') // https://bitbucket.org/surfstudio/jenkins-pipeline-lib/
import ru.surfstudio.ci.pipeline.PrPipelineAndroid
import ru.surfstudio.ci.stage.StageStrategy

//init
def pipeline = new PrPipelineAndroid(this)
pipeline.init()

//customization
pipeline.getStage(pipeline.INSTRUMENTATION_TEST).strategy = StageStrategy.SKIP_STAGE
pipeline.getStage(pipeline.STATIC_CODE_ANALYSIS).strategy = StageStrategy.SKIP_STAGE
pipeline.buildGradleTask = "clean assemble"

//run
pipeline.run()
