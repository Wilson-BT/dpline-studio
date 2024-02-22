<template>
    <div id="codeEditBox" class="codeEditBox"></div>
</template>
  
<script lang="ts" setup>
import * as monaco from 'monaco-editor';
import { onMounted, ref, toRaw } from 'vue';
import { format } from 'sql-formatter';
import {
    pythonCompletion, sqlCompletion, javaCompletion
} from "/@/utils/vsLanguages.js";
import mittBus from '/@/utils/mitt';

const text = ref('')
const editor = ref()
const editorTheme = ref("vs")
const language = ref("sql")
const initEditor = () => {
    // 初始化编辑器，确保dom已经渲染
    editor.value = monaco.editor.create(document.getElementById('codeEditBox') as HTMLElement, {
        value: text.value, // 编辑器初始显示文字
        language: 'sql', // 语言支持自行查阅demo
        automaticLayout: true, // 自适应布局  
        theme: 'vs', // 官方自带三种主题vs, hc-black, or vs-dark
        foldingStrategy: 'indentation',
        renderLineHighlight: 'all', // 行亮
        selectOnLineNumbers: true, // 显示行号
        // 右边代码小窗口地图
        minimap: {
            enabled: true,
        },
        readOnly: false, // 只读
        fontSize: 14, // 字体大小
        scrollBeyondLastLine: false, // 取消代码后面一大段空白 
        overviewRulerBorder: true, // 要滚动条的边框
        contextmenu: false  // 上下文菜单开关Í
    });

    // 监听值的变化
    // editor.value.onDidChangeModelContent((val) => {
    //   console.log(val.changes[0].text)
    // })

    // 创建代码提醒
    pythonCompletion
    sqlCompletion
    javaCompletion
}

mittBus.on('editorFormat', () => {
    handleFormat();
});

const handleTheme = () => {
    monaco.editor.setTheme(editorTheme.value)
}
const handleLanguage = (item: string) => {
    language.value = item
    monaco.editor.setModelLanguage(toRaw(editor.value).getModel(), language.value)
    // console.log(toRaw(editor.value).getModel().getLanguageId())
}
const handleFormat = () => {
    let lan = toRaw(editor.value).getModel().getLanguageId()
    let content = toRaw(editor.value).getValue()
    if (lan == 'sql') {
        toRaw(editor.value).setValue(format(content))
    }
    else if (lan == 'json') {
        toRaw(editor.value).trigger('anyString', 'editor.action.formatDocument')
        toRaw(editor.value).setValue(content)
    }
}
onMounted(() => {
    initEditor();
});
</script>
  
<style scoped>
.codeEditBox {
    height: 100%;
    width: 100%;
}
</style>
