import * as monaco from 'monaco-editor'
import { language as pythonLanguage } from 'monaco-editor/esm/vs/basic-languages/python/python.js';
import { language as sqlLanguage } from 'monaco-editor/esm/vs/basic-languages/sql/sql.js';
import { language as javaLanguage } from 'monaco-editor/esm/vs/basic-languages/java/java.js';

const pythonCompletion = monaco.languages.registerCompletionItemProvider('python', {
    provideCompletionItems: function () {
        let suggestions = [];
        pythonLanguage.keywords.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Keyword,
                insertText: item
            });
        })
        return {
            suggestions: suggestions
        };
    },
});

const sqlCompletion = monaco.languages.registerCompletionItemProvider('sql', {
    provideCompletionItems: function () {
        let suggestions = [];
        sqlLanguage.keywords.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Keyword,
                insertText: item
            });
        })
        sqlLanguage.operators.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Operator,
                insertText: item
            });
        })
        sqlLanguage.builtinFunctions.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Function,
                insertText: item
            });
        })
        sqlLanguage.builtinVariables.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Variable,
                insertText: item
            });
        })
        return {
            suggestions: suggestions
        };
    },
});

const javaCompletion = monaco.languages.registerCompletionItemProvider('java', {
    provideCompletionItems: function () {
        let suggestions = [];
        javaLanguage.keywords.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Keyword,
                insertText: item
            });
        })
        javaLanguage.operators.forEach(item => {
            suggestions.push({
                label: item,
                kind: monaco.languages.CompletionItemKind.Operator,
                insertText: item
            });
        })
        suggestions.push({
            label: "Class",
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: "Class"
        });
        suggestions.push({
            label: "Interface",
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: "Interface"
        });
        return {
            suggestions: suggestions
        };
    },
});


export {
    pythonCompletion,
    sqlCompletion,
    javaCompletion
};
