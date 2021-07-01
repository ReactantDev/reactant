package dev.reactant.devbridge

import io.ktor.application.ApplicationCall
import io.ktor.util.pipeline.PipelineContext

typealias Action = suspend PipelineContext<Unit, ApplicationCall>.(Unit) -> Unit
