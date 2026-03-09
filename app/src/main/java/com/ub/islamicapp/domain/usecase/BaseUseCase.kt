package com.ub.islamicapp.domain.usecase

import kotlinx.coroutines.flow.Flow

interface BaseUseCase<In, Out> {
    suspend operator fun invoke(params: In): Flow<Out>
}
