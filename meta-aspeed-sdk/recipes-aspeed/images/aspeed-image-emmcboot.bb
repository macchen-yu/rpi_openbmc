# According to the design of AST2600, it is required to add the image length at
# offset 0x28 in the header of boot image to support boot from eMMC and the size
# of boot image should align block size. (512bytes)
# Users do not need to add image length in the header of boot image and the size
# of boot image does not need to align block size, either. (512 bytes) for AST2700.
# This recipe is only used for AST2600. Users should not bitbake this recipe if
# BMC SOC is not AST2600.

DESCRIPTION = "Generate image boot from eMMC for ASPEED BMC SoCs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${ASPEEDSDKBASE}/LICENSE;md5=a3740bd0a194cd6dcafdc482a200a56f"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

DEPENDS = "aspeed-image-tools-native"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

inherit deploy

# Image composition
UBOOT_SUFFIX ?= "bin"

ASPEED_IMAGE_UBOOT_SPL_IMAGE ?= "u-boot-spl"
ASPEED_IMAGE_UBOOT_IMAGE ?= "u-boot"
ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE ?= "emmc_${ASPEED_IMAGE_UBOOT_SPL_IMAGE}"
ASPEED_EMMC_IMAGE_MERGE_BOOT_IMAGE ?= "emmc_image-u-boot"

# machine configs did not have the SPL size
ASPEED_EMMC_IMAGE_UBOOT_SPL_SIZE_KB:aspeed-g6 ?= "64"

ASPEED_SECURE_BOOT ?= "${@bb.utils.contains('MACHINE_FEATURES', 'ast-secure', 'yes', 'no', d)}"
ASPEED_BOOT_EMMC ?= "${@bb.utils.contains('MACHINE_FEATURES', 'ast-mmc', 'yes', 'no', d)}"

OUTPUT_IMAGE_DIR ?= "${S}/output"
SOURCE_IMAGE_DIR ?= "${S}/source"

do_mk_empty_image() {
    rm -rf ${SOURCE_IMAGE_DIR}
    rm -rf ${OUTPUT_IMAGE_DIR}
    install -d ${SOURCE_IMAGE_DIR}
    install -d ${OUTPUT_IMAGE_DIR}

    # Assemble the flash image
    dd if=/dev/zero bs=1k count=${MMC_UBOOT_SIZE} | \
        tr '\000' '\377' > ${OUTPUT_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_MERGE_BOOT_IMAGE}
}

do_mk_emmc_boot_image_g6() {
    do_mk_empty_image

    if [ "${ASPEED_SECURE_BOOT}" = "no" ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX} ${SOURCE_IMAGE_DIR}
        python3 ${STAGING_BINDIR_NATIVE}/gen_emmc_boot_image.py ${SOURCE_IMAGE_DIR}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX} ${SOURCE_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}
        install -m 0644 ${SOURCE_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX} ${OUTPUT_IMAGE_DIR}
    else
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX} ${SOURCE_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}
        if [ -f ${DEPLOY_DIR_IMAGE}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}.unsigned ]; then
            install -m 0644 ${DEPLOY_DIR_IMAGE}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}.unsigned ${SOURCE_IMAGE_DIR}
            python3 ${STAGING_BINDIR_NATIVE}/gen_emmc_boot_image.py ${SOURCE_IMAGE_DIR}/${ASPEED_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}.unsigned ${SOURCE_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}.unsigned
            install -m 0644 ${SOURCE_IMAGE_DIR}/${ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE}.${UBOOT_SUFFIX}.unsigned ${OUTPUT_IMAGE_DIR}
        fi
    fi
}

do_mk_emmc_boot_image_g7() {
    do_mk_empty_image
}

do_deploy_emmc_image() {
    # Deploy image for boot from emmc
    install -d ${DEPLOYDIR}
    install -m 644 ${OUTPUT_IMAGE_DIR}/* ${DEPLOYDIR}/.
}

python do_deploy() {
    import subprocess

    if d.getVar('ASPEED_BOOT_EMMC', True) != "yes":
        bb.fatal("Only support Boot from EMMC mode run this task")

    bootmcu_fw_binary = d.getVar('BOOTMCU_FW_BINARY', True)
    spl_binary = d.getVar('SPL_BINARY', True)
    soc_family = d.getVar('SOC_FAMILY', True)

    if bootmcu_fw_binary and spl_binary:
        bb.fatal('SPL_BINARY and BOOTMCU_FW_BINARY should not be set at the same time')

    if soc_family == "aspeed-g7":
        bb.fatal("AST2700 Boot from EMMC mode should not run this task")
    elif soc_family == "aspeed-g6":
        if not spl_binary:
            bb.fatal("Boot from EMMC only support SPL")
        bb.build.exec_func("do_mk_emmc_boot_image_g6", d)
    else:
        bb.fatal("Unsupport Machine")

    emmc_boot_image = os.path.join(d.getVar('OUTPUT_IMAGE_DIR', True),
                                   d.getVar('ASPEED_EMMC_IMAGE_MERGE_BOOT_IMAGE', True))


    def _append_image(imgpath, start_kb, finish_kb):
        imgsize = os.path.getsize(imgpath)
        maxsize = (finish_kb - start_kb) * 1024
        bb.debug(1, 'Considering file size=' + str(imgsize) + ' name=' + imgpath)
        bb.debug(1, 'Spanning start=' + str(start_kb) + 'K end=' + str(finish_kb) + 'K')
        bb.debug(1, 'Compare needed=' + str(imgsize) + ' available=' + str(maxsize) + ' margin=' + str(maxsize - imgsize))
        if imgsize > maxsize:
            bb.fatal("Image '%s' is too large!" % imgpath)

        subprocess.check_call(['dd', 'bs=1k', 'conv=notrunc',
                              'seek=%d' % start_kb,
                              'if=%s' % imgpath,
                              'of=%s' % emmc_boot_image])


    uboot_offset = 0

    # bootmcu
    if bootmcu_fw_binary:
        bootmcu_fw_finish_kb = int(d.getVar('FLASH_BMCU_SIZE', True))
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                                   '%s' % (d.getVar('BOOTMCU_FW_BINARY', True))),
                      0,
                      bootmcu_fw_finish_kb)
        uboot_offset += bootmcu_fw_finish_kb

    # spl
    if spl_binary:
        spl_finish_kb = int(d.getVar('ASPEED_EMMC_IMAGE_UBOOT_SPL_SIZE_KB', True))
        _append_image(os.path.join(d.getVar('SOURCE_IMAGE_DIR', True),
                                '%s.%s' % (
                                d.getVar('ASPEED_EMMC_IMAGE_UBOOT_SPL_IMAGE', True),
                                d.getVar('UBOOT_SUFFIX', True))),
                      0,
                      spl_finish_kb)
        uboot_offset += spl_finish_kb

    # uboot
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                  '%s.%s' % (
                  d.getVar('ASPEED_IMAGE_UBOOT_IMAGE', True),
                  d.getVar('UBOOT_SUFFIX', True))),
                  uboot_offset,
                  int(d.getVar('MMC_UBOOT_SIZE', True)))

    bb.build.exec_func("do_deploy_emmc_image", d)
}

do_deploy[depends] += " \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
    "

addtask deploy before do_build after do_compile

