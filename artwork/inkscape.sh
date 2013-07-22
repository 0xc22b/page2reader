#! bin/bash

path="/Users/wit/Workspace/Misc/page2reader/artwork"

/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon-small.svg --export-png=$path/icon-16x16.png -w16 -h16
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon-small.svg --export-png=$path/icon-19x19.png -w19 -h19

/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon.svg --export-png=$path/icon-19x19.png -w32 -h32
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon.svg --export-png=$path/icon-48x48.png -w48 -h48
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon.svg --export-png=$path/icon-64x64.png -w64 -h64
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/icon.svg --export-png=$path/icon-128x128.png -w128 -h128

/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-48x48.png -w48 -h48
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-57x57.png -w57 -h57
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-72x72.png -w72 -h72
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-96x96.png -w96 -h96
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-114x114.png -w114 -h114
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-144x144.png -w144 -h144
/Applications/Inkscape.app/Contents/Resources/bin/inkscape $path/logo.svg --export-png=$path/logo-512x512.png -w512 -h512
