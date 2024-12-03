package com.verygoodsecurity.vgsshow.util.extension

import android.os.Build

internal val isLollipopOrGreater
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

internal val isOreoOrGreater
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

internal val isMarshmallowOrGreater
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

internal val isNougatOrGreater get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
