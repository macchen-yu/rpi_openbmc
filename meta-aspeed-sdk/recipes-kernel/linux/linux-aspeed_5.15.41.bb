KBRANCH = "aspeed-master-v5.15"
LINUX_VERSION ?= "5.15.41"

# Tag for v00.05.13
SRCREV = "2afe1baf6e7a913c4d3c5a5fb75abf304448a7c7"

LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

require linux-aspeed.inc

DEPENDS += "lzop-native"
DEPENDS += "${@bb.utils.contains('MACHINE_FEATURES', 'ast-secure', 'aspeed-secure-config-native', '', d)}"

SRC_URI:append = " file://ipmi_ssif.cfg "
SRC_URI:append = " file://mtd_test.cfg "
SRC_URI:append = " file://crpyto_manager.cfg "
SRC_URI:append:spi-nor-ecc = " file://jffs2_writebuffer.cfg "
