<template>
  <div class="registrer">
    <div class="title">Sign Up</div>
    <div class="domain">Domain: {{ domain }}</div>
    <div class="tips">
      Already have an account?
      <span @click="$router.push({ path: '/login' })">SIGN IN</span>
    </div>
    <div class="enter_ons">
      <el-input
        placeholder="Create a new account name"
        v-model="ons"
        class="input-with-select"
      >
        <el-button @click="apply" type="primary" slot="append">Apply</el-button>
      </el-input>
    </div>
    <div class="claim_tips">
      <span v-if="claim_flag">Scan to perform authorization.</span>
      <span v-if="account_flag">Scan to confirm registration.</span>
    </div>
    <div class="qrcode">
      <img :src="url" alt />
    </div>
  </div>
</template>

<script>
import QRCode from 'qrcode'
export default {
  data() {
    return {
      ons: '',
      url: '',
      dataId: '',
      checkTimer: null,
      domain: 'on.ont',
      claim_flag: false,
      account_flag: false,
      claimCheckId: '',
      claimTimer: null
    }
  },
  methods: {
    createQRcode(params) {
      params = JSON.stringify(params)
      QRCode.toDataURL(params)
        .then(url => {
          this.url = url
        })
        .catch(err => {})
    },
    apply() {
      if (this.ons === '') {
        this.$message({
          message: 'Please Input Your account name!',
          center: true,
          type: 'success'
        })
        return
      }
      // claim 授权
      this.claim_flag = true
      this.postChaim()
    },
    async sendOns() {
      try {
        let params = {
          userName: this.ons
        }
        let res = await this.$store.dispatch('sendONS', params)
        console.log('res', res)

        if (res.data.desc === 'SUCCESS' && res.data.error === 0) {
          let qrcodeParams = res.data.result.qrCode
          this.dataId = res.data.result.id
          console.log('qrcodeParams', qrcodeParams)
          console.log('dataId', this.dataId)
          // 生成注册二维码
          await this.createQRcode(qrcodeParams)
          // 检查注册结果
          this.checkTimer = setInterval(() => {
            this.checkResult()
          }, 3000)
        } else {
          this.$message({
            message: 'Sign Up Fail!',
            center: true,
            type: 'error'
          })
          return
        }
      } catch (error) {
        throw error
        return false
      }
    },
    async checkResult() {
      try {
        let res = await this.$store.dispatch('checkSignUp', this.dataId)
        console.log('checkout', res)
        if (res.data.desc === 'SUCCESS') {
          if (res.data.result.result === '1') {
            this.$message({
              message: 'Sign Up Successfuly!',
              center: true,
              type: 'success'
            })
            clearInterval(this.checkTimer)
            this.$router.push({ path: '/login' })
            return true
          } else if (res.data.result.result === '0') {
            clearInterval(this.checkTimer)
            this.$message({
              message: 'Sign Up Fail!',
              center: true,
              type: 'error'
            })
            return false
          } else if (res.data.result.result === '2') {
            clearInterval(this.checkTimer)
            this.$message({
              message: 'Already Registed!',
              center: true,
              type: 'error'
            })
            return false
          } else {
          }
        } else {
          clearInterval(this.checkTimer)
          this.$message({
            message: 'Sign Up Fail!',
            center: true,
            type: 'error'
          })
          return false
        }
      } catch (error) {
        clearInterval(this.checkTimer)
        return false
      }
    },
    async postChaim() {
      try {
        let res = await this.$store.dispatch('getClaimMsg')
        console.log('claim res', res)
        if (res.data.desc === 'SUCCESS') {
          let qrcodeParams = res.data.result
          this.claimCheckId = res.data.result.id
          console.log('qrcodeParams', qrcodeParams)
          console.log('dataId', this.dataId)
          // 生成claim二维码
          await this.createQRcode(qrcodeParams)
          // 检查注册结果
          this.claimTimer = setInterval(() => {
            this.CheckClaimResult()
          }, 3000)
        } else {
          this.$message({
            message: 'Sign Up Fail!',
            center: true,
            type: 'error'
          })
          return
        }
      } catch (error) {
        // this.$message({
        //   message: 'Authorization Failure!',
        //   center: true,
        //   type: 'error'
        // });
        return false
      }
    },
    async CheckClaimResult() {
      try {
        let res = await this.$store.dispatch('checkClaim', this.claimCheckId)
        console.log('checkout', res)
        if (res.data.desc === 'SUCCESS') {
          if (res.data.result.result === '1') {
            this.$message({
              message: 'Authorized Success!',
              center: true,
              type: 'success'
            })
            clearInterval(this.claimTimer)
            // this.$router.push({ path: '/login' })
            this.claim_flag = false
            this.account_flag = true
            this.sendOns()
            return true
          } else if (res.data.result.result === '0') {
            clearInterval(this.claimTimer)
            this.$message({
              message: 'Authorization Failure!',
              center: true,
              type: 'error'
            })
            return false
          } else {
          }
        } else {
          clearInterval(this.claimTimer)
          this.$message({
            message: 'Authorization Failure!',
            center: true,
            type: 'error'
          })
          return false
        }
      } catch (error) {
        clearInterval(this.claimTimer)
        // this.$message({
        //   message: error,
        //   center: true,
        //   type: 'error'
        // });
        return false
      }
    }
  },
  beforeDestroy() {
    clearInterval(this.checkTimer)
    clearInterval(this.claimTimer)
  }
}
</script>

<style lang="less" scoped>
.registrer {
  width: 100%;
  height: 100%;
  max-width: 1020px;
  margin: 0 auto;
  .domain {
    font-size: 20px;
    line-height: 36px;
  }
  .claim_tips {
    font-size: 20px;
    margin-top: 30px;
  }
  .title {
    font-size: 50px;
    font-weight: bold;
    line-height: 200px;
    padding-top: 100px;
  }
  .tips {
    font-size: 20px;
    span {
      color: #2472cc;
      text-decoration: underline;
      cursor: pointer;
    }
  }
  .enter_ons {
    max-width: 600px;
    margin: 0 auto;
    margin-top: 40px;
  }
  .qrcode {
    width: 200px;
    height: 200px;
    margin: 0 auto;
    margin-top: 50px;
    img {
      width: 100%;
      height: 100%;
      display: block;
    }
  }
}
</style>
