# Refer to meta-zephyr/meta-zephyr-core/recipes-kernel/zephyr-kernel/zephyr-kernel-src-x.x.x.inc
# creating this file
SUMMARY = "BootMCU execute Zephyr OS with aspeed-irot firmware"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PROVIDES += "virtual/bootmcu"

SRCREV_mbedtls = "fb36f3fe20f9f62e67b1a20c0cfe0a6788ec2bf6"
SRCREV_cmsis = "4b96cbb174678dcd3ca86e11e1f24bc5f8726da0"

SRC_URI_ZEPHYR = "git://gerrit.aspeed.com:29418/zephyr;protocol=ssh"
SRC_URI_ASPEED_ZEPHYR_PROJECT = "gitsm://gerrit.aspeed.com:29418/aspeed-zephyr-project;protocol=ssh"
SRC_URI_ZEPHYR_MCUBOOT = "git://gerrit.aspeed.com:29418/mcuboot;protocol=ssh"
SRC_URI_ZEPHYR_MBEDTLS = "git://github.com/zephyrproject-rtos/mbedtls;protocol=https"
SRC_URI_ZEPHYR_CMSIS = "git://github.com/zephyrproject-rtos/cmsis;protocol=https"

SRC_URI = "\
    ${SRC_URI_ZEPHYR};name=zephyr;branch=${ZEPHYR_BRANCH};destsuffix=git/zephyr \
    ${SRC_URI_ASPEED_ZEPHYR_PROJECT};name=bootmcu;branch=${ASPEED_ZEPHYR_PROJECT_BRANCH};destsuffix=git/aspeed-zephyr-project \
    ${SRC_URI_ZEPHYR_MCUBOOT};name=mcuboot;branch=${ZEPHYR_MCUBOOT_BRANCH};destsuffix=git/bootloader/mcuboot \
    ${SRC_URI_ZEPHYR_MBEDTLS};name=mbedtls;nobranch=1;destsuffix=git/modules/crypto/mbedtls \
    ${SRC_URI_ZEPHYR_CMSIS};name=cmsis;nobranch=1;destsuffix=git/modules/hal/cmsis \
"

ZEPHYR_MODULES = "\
${S}/aspeed-zephyr-project\;\
${S}/bootloader/mcuboot\;\
${S}/modules/crypto/mbedtls\;\
${S}/modules/hal/cmsis\;\
"

INSANE_SKIP:${PN} += "arch"

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
