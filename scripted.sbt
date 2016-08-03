
scriptedSettings

scriptedLaunchOpts += "-Xmx1024m"

scriptedLaunchOpts <+= version apply { v => "-Dproject.version="+v }

// scriptedBufferLog := false
