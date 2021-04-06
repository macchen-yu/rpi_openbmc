# Create build environment
## Prerequisite
### Ubuntu 18.04
```
sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib \
     build-essential chrpath socat libsdl1.2-dev xterm
```

### Fedora
```
sudo yum install gawk make wget tar bzip2 gzip python unzip perl patch \
     diffutils diffstat git cpp gcc gcc-c++ glibc-devel texinfo chrpath \
     ccache perl-Data-Dumper perl-Text-ParseWords perl-Thread-Queue socat \
     findutils which SDL-devel xterm
```

Reference:
- [OpenBMC/README.md](https://github.com/openbmc/openbmc#1-prerequisite)
- [Yocto Quick Start](https://www.yoctoproject.org/docs/1.8/yocto-project-qs/yocto-project-qs.html#the-linux-distro)

## Get the source code
```
git clone https://github.com/openbmc/openbmc
git clone ssh://git@192.168.10.30:7999/bmc/meta-aspeed-sdk.git
cd openbmc
```

1. AST2600

```
TEMPLATECONF=../meta-aspeed-sdk/meta-ast2600-sdk/conf/ . openbmc-env [BUILD_DIR]
```

2. AST2500

```
TEMPLATECONF=../meta-aspeed-sdk/meta-ast2500-sdk/conf/ . openbmc-env [BUILD_DIR]
```

## Build OpenBMC firmware
```
bitbake obmc-phosphor-image
```

## Build SDK Image (all.bin)
1. Apply the following change to `conf/local.conf`:

```
- require conf/machine/include/obmc-bsp-common.inc
+ #require conf/machine/include/obmc-bsp-common.inc

# ASPEED initramfs if build aspeed-image-norootfs
- #INITRAMFS_IMAGE = "aspeed-image-initramfs"
+ INITRAMFS_IMAGE = "aspeed-image-initramfs"
```

2. Change device tree from `aspeed-ast2600-obmc.dtb` to `aspeed-ast2600-evb.dtb` in `meta-ast2600-sdk/conf/machine/${MACHINE}.conf` file, e.g.,

```
# ASPEED ast2600 evb dtb file if build aspeed-image-sdk
- #KERNEL_DEVICETREE = "aspeed-ast2600-evb.dtb"
- KERNEL_DEVICETREE = "aspeed-ast2600-obmc.dtb"
+ KERNEL_DEVICETREE = "aspeed-ast2600-evb.dtb"
+ #KERNEL_DEVICETREE = "aspeed-ast2600-obmc.dtb"
```

3. Then trigger the build.

```
bitbake aspeed-image-sdk
```

## Build different machine config
If you want to create different machine, e.g. emmc, secure-boot, you could edit `conf/local.conf`, please unmark the desired `MACHINE`.
```
- MACHINE ??= "ast2600-default"
+ #MACHINE ??= "ast2600-default"
#MACHINE ??= "ast2600-a1"
- #MACHINE ??= "ast2600-emmc"
+ MACHINE ??= "ast2600-emmc"
#MACHINE ??= "ast2600-secure-gcm"
#MACHINE ??= "ast2600-secure-rsa2048-sha256"
#MACHINE ??= "ast2600-secure-rsa2048-sha256-ncot"
#MACHINE ??= "ast2600-secure-rsa2048-sha256-o1"
#MACHINE ??= "ast2600-secure-rsa2048-sha256-o2-priv"
#MACHINE ??= "ast2600-secure-rsa2048-sha256-o2-pub"
#MACHINE ??= "ast2600-secure-rsa3072-sha384"
#MACHINE ??= "ast2600-secure-rsa3072-sha384-o1"
#MACHINE ??= "ast2600-secure-rsa3072-sha384-o2-priv"
#MACHINE ??= "ast2600-secure-rsa3072-sha384-o2-pub"
#MACHINE ??= "ast2600-secure-rsa4096-sha512"
#MACHINE ??= "ast2600-secure-rsa4096-sha512-o1"
#MACHINE ??= "ast2600-secure-rsa4096-sha512-o2-priv"
#MACHINE ??= "ast2600-secure-rsa4096-sha512-o2-pub"
```

Then the image will be built according to the setting of `meta-aspeed-sdk/meta-ast2600-sdk/conf/machine/${MACHINE}.conf`.

# Output image
After you successfully built the image, the image file can be found in: `[BUILD_DIR]/tmp/work/deploy/images/${MACHINE}/`

## OpenBMC firmware

### Boot from SPI image
- `image-bmc`: whole flash image
- `image-u-boot`: u-boot-spl.bin + u-boot.bin
- `image-kernel`: Linux Kernel FIT image
- `image-rofs`: read-only root file system

### Boot from eMMC image
- `emmc_image-u-boot`: u-boot-spl.bin + u-boot.bin processed with gen\_emmc\_image.py for boot partition
- `obmc-phosphor-image-${MACHINE}.wic.xz`: compressed emmc flash image for user data partition

## SDK Image
- `all.bin`: whole flash image
