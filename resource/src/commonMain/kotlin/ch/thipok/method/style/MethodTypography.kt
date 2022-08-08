package ch.thipok.method.style

import ch.thipok.method.resource.Resource

@Suppress("MagicNumber")
object MethodTypography {
    object DisplayLarge : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 57.0
        override val lineHeight = 64.0
    }
    object DisplayMedium : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 45.0
        override val lineHeight = 52.0
    }
    object DisplaySmall : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 36.0
        override val lineHeight = 44.0
    }

    object HeadlineLarge : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 32.0
        override val lineHeight = 40.0
    }
    object HeadlineMedium : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 28.0
        override val lineHeight = 36.0
    }
    object HeadlineSmall : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 24.0
        override val lineHeight = 32.0
    }

    object TitleLarge : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 22.0
        override val lineHeight = 28.0
    }
    object TitleMedium : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 16.0
        override val lineHeight = 24.0
    }
    object TitleSmall : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 14.0
        override val lineHeight = 20.0
    }

    object LabelLarge : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 14.0
        override val lineHeight = 20.0
    }
    object LabelMedium : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 12.0
        override val lineHeight = 16.0
    }
    object LabelSmall : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 11.0
        override val lineHeight = 16.0
    }

    object BodyLarge : TypographySet {
        override val font = Resource.fonts.NotoSans.medium
        override val size = 16.0
        override val lineHeight = 24.0
    }
    object BodyMedium : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 14.0
        override val lineHeight = 20.0
    }
    object BodySmall : TypographySet {
        override val font = Resource.fonts.NotoSans.regular
        override val size = 12.0
        override val lineHeight = 16.0
    }
}