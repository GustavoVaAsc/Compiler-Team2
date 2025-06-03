package unam.fi.compilers.team2.parser

sealed class Action {
    data class Shift(val state_id: Int) : Action()
    data class Reduce(val production: Production) : Action()
    object Accept : Action()
}