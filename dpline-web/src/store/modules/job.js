const state = {
  jobInfo: null,
  configInfoActive: '',
  alarmConfigActive: ''
}

const mutations = {
  setJobInfo: (state, data) => {
    state.jobInfo = data
  },
  setConfigInfoActive: (state, data) => {
    state.configInfoActive = data
  },
  setAlarmConfigActive: (state, data) => {
    state.alarmConfigActive = data
  },
}

const actions = {
  setJobInfo ({ commit }, data) {
    commit('setJobInfo', data)
  },
  setConfigInfoActive ({ commit }, data) {
    commit('setConfigInfoActive', data)
  },
  setAlarmConfigActive ({ commit }, data) {
    commit('setAlarmConfigActive', data)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
