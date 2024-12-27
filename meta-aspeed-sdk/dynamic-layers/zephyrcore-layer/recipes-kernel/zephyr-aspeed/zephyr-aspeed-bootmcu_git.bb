require recipes-kernel/zephyr-kernel/zephyr-image.inc
require zephyr-aspeed-src.inc

SUMMARY = "BootMCU execute Zephyr OS"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PROVIDES += "virtual/bootmcu"
PV = "1.0+git"

# zephyr
SRCREV_zephyr = "5cef78e7ae6e1e85680acbed7f8667feafdd4867"
ZEPHYR_BRANCH = "aspeed-main-v3.7.0"

# bootmcu
SRCREV_bootmcu = "8e1f856aaac56d68f8ea65a0230bcec32c6a9d5a"
ASPEED_ZEPHYR_PROJECT_BRANCH = "aspeed-master"

# mcuboot
SRCREV_mcuboot = "ce9bdf834557ee71b3ba87c32a16990f306ee8ee"
ZEPHYR_MCUBOOT_BRANCH = "aspeed-main-v3.4.0"

ZEPHYR_SRC_DIR = "${S}/aspeed-zephyr-project/apps/aspeed-irot"

DEPENDS += "dtc-native fmc-imgtool-native"

ZEPHYR_MAKE_OUTPUT:append = " fmc-zephyr.bin"

# export CRYPTOGRAPHY_OPENSSL_NO_LEGACY variable to fix the following errors.
# OpenSSL 3.0 legacy provider failed to load
# https://github.com/pyca/cryptography/issues/10598
do_create_fmc_image() {
    export CRYPTOGRAPHY_OPENSSL_NO_LEGACY=1

    cd ${STAGING_LIBDIR_NATIVE}/${PYTHON_DIR}/fmc-imgtool
    python3 main.py \
        --verbose \
        --version 2 \
        --input ${B}/zephyr/zephyr.bin \
        --output ${B}/zephyr/fmc-zephyr.bin
    cd -
}

addtask create_fmc_image before do_install do_deploy after do_compile

do_deploy:append() {
    cd ${DEPLOYDIR}
    ln -sf fmc-${BPN}.bin bootmcu-fmc.bin
    cd -
}
