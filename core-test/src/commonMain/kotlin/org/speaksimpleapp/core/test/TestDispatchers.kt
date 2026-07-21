package org.speaksimpleapp.core.test

import kotlinx.coroutines.CoroutineDispatcher
import org.speaksimpleapp.core.common.coroutines.CoroutineDispatchers

class TestDispatchers(
    override val main: CoroutineDispatcher,
) : CoroutineDispatchers
