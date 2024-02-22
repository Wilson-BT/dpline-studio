<template>
  <div class="splitpanes-container layout-padding">
    <div class="container-main">
      <splitpanes class="default-theme" :horizontal="true">
        <pane size="4" min-size="4" max-size="4">
          <HeadMenu></HeadMenu>
        </pane>
        <pane size="96">
          <splitpanes class="default-theme edit-container-main">
            <pane size="20" class="edit-container-main">
              <ProjectTree></ProjectTree>
            </pane>
            <pane size="80">
              <splitpanes class="default-theme" :horizontal="true">
                <pane size="60">
                  <VsEditor></VsEditor>
                </pane>
                <pane size="40">
                  <BottomConsole></BottomConsole>
                </pane>
              </splitpanes>
            </pane>
          </splitpanes>
        </pane>
      </splitpanes>

    </div>
  </div>
</template>

<script lang="ts" setup>
import { Splitpanes, Pane } from 'splitpanes';
import 'splitpanes/dist/splitpanes.css';
import ProjectTree from "./component/projectTree.vue";
import HeadMenu from "./component/headMenu.vue";
import BottomConsole from "/@/views/home/component/bottomConsole.vue";
import VsEditor from "./component/vsEditor.vue";

</script>

<style scoped lang="scss">
.splitpanes__pane {
  align-items: center;
  position: relative;
}

.container-main {
  height: 100%;
  width: 100%;
}

.edit-container-main {
  height: 100%;
  width: 100%;
}
</style>
