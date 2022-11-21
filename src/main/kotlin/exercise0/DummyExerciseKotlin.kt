package exercise0

import de.hpi.dbs2.ChosenImplementation
import de.hpi.dbs2.exercise0.DummyExercise

@ChosenImplementation(true)
class DummyExerciseKotlin : DummyExercise {
    override val yourGroupIdentifier: String
        get() = "I"
}
