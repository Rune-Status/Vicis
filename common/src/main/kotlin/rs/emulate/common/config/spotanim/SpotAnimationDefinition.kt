package rs.emulate.common.config.spotanim

import rs.emulate.common.config.Definition


class SpotAnimationDefinition(
    override val id: Int,
    var model: Int = 0,
    var sequenceId: Int = -1,
    var planarScale: Int = 128,
    var verticalScale: Int = 128,
    var orientation: Int = 0,
    var modelBrightness: Int = 0,
    var modelDiffusion: Int = 0,
    var originalColours: IntArray = IntArray(size = 10),
    var replacementColours: IntArray = IntArray(size = 10)
) : Definition
