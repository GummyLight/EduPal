<template>
  <div class="markdown-demo-container">
    <!-- 顶部导航栏 -->
    <header class="demo-header">
      <div class="header-content">
        <div class="logo">
          <h1>Markdown <span class="highlight">Demo</span></h1>
        </div>
        <nav class="nav-menu">
          <a href="#basic" @click="scrollToSection('basic')">基础语法</a>
          <a href="#math" @click="scrollToSection('math')">数学公式</a>
          <a href="#advanced" @click="scrollToSection('advanced')">高级功能</a>
          <a href="#code" @click="scrollToSection('code')">代码高亮</a>
          <a href="#interactive" @click="scrollToSection('interactive')">交互功能</a>
        </nav>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="demo-main">
      <div class="container">
        <!-- 介绍部分 -->
        <section class="intro-section">
          <div class="intro-content">
            <h1 class="intro-title">强大的 Markdown 渲染引擎</h1>
            <p class="intro-description">
              基于 markdown-it 构建的现代化 Markdown 渲染器，支持丰富的语法扩展和代码高亮功能
            </p>
            <div class="intro-actions">
              <button @click="toggleEditor" class="primary-btn">
                {{ showEditor ? '隐藏编辑器' : '显示编辑器' }}
              </button>
              <button @click="copyAllMarkdown" class="secondary-btn">复制所有代码</button>
            </div>
          </div>
        </section>

        <!-- 编辑器和预览区域 -->
        <div class="editor-preview-container" :class="{ 'show-editor': showEditor }">
          <!-- 编辑器 -->
          <div v-if="showEditor" class="editor-panel">
            <div class="panel-header">
              <h3>Markdown 编辑器</h3>
              <button @click="resetToDemo" class="reset-btn">重置为示例</button>
            </div>
            <textarea 
              v-model="markdownContent" 
              class="markdown-editor"
              placeholder="在这里输入 Markdown 内容..."
            ></textarea>
          </div>

          <!-- 预览面板 -->
          <div class="preview-panel">
            <div class="panel-header">
              <h3>渲染预览</h3>
              <button @click="exportHTML" class="export-btn">导出 HTML</button>
            </div>
            <div class="markdown-preview" v-html="renderedMarkdown"></div>
          </div>
        </div>

        <!-- 功能展示区域 -->
        <div class="features-showcase">
          <!-- 基础语法 -->
          <section id="basic" class="feature-section">
            <h2 class="section-title">基础语法</h2>
            <div class="feature-grid">
              <div class="feature-card">
                <h3>标题层级</h3>
                <div class="code-preview">
                  <pre><code># H1 标题
## H2 标题
### H3 标题
#### H4 标题
##### H5 标题
###### H6 标题</code></pre>
                </div>
              </div>

              <div class="feature-card">
                <h3>文本样式</h3>
                <div class="code-preview">
                  <pre><code>**粗体文本**
*斜体文本*
~~删除线~~
`行内代码`
> 引用文本</code></pre>
                </div>
              </div>

              <div class="feature-card">
                <h3>列表</h3>
                <div class="code-preview">
                  <pre><code>- 无序列表项 1
- 无序列表项 2
  - 嵌套项

1. 有序列表项 1
2. 有序列表项 2
   1. 嵌套项</code></pre>
                </div>
              </div>

              <div class="feature-card">
                <h3>链接和图片</h3>
                <div class="code-preview">
                  <pre><code>[链接文本](https://example.com)
![图片描述](image.jpg)
[参考链接][1]

[1]: https://example.com</code></pre>
                </div>
              </div>
            </div>
          </section>

          <!-- 数学公式 -->
          <section id="math" class="feature-section">
            <h2 class="section-title">数学公式 (LaTeX)</h2>
            <div class="math-examples">
              <div class="math-category">
                <h3>基础公式</h3>
                <div class="math-grid">
                  <div class="math-card">
                    <h4>行内公式</h4>
                    <div class="code-preview">
                      <pre><code>$E = mc^2$
$a^2 + b^2 = c^2$
$\pi \approx 3.14159$</code></pre>
                    </div>
                    <div class="math-render" v-html="renderMarkdown('$E = mc^2$, $a^2 + b^2 = c^2$, $\\pi \\approx 3.14159$')"></div>
                  </div>

                  <div class="math-card">
                    <h4>分数和根号</h4>
                    <div class="code-preview">
                      <pre><code>$$\frac{a}{b} = \frac{c}{d}$$

$$\sqrt{x^2 + y^2}$$

$$\sqrt[3]{8} = 2$$</code></pre>
                    </div>
                    <div class="math-render" v-html="renderMarkdown('$$\\frac{a}{b} = \\frac{c}{d}$$\\n$$\\sqrt{x^2 + y^2}$$\\n$$\\sqrt[3]{8} = 2$$')"></div>
                  </div>
                </div>
              </div>

              <div class="math-category">
                <h3>高级数学</h3>
                <div class="math-grid">
                  <div class="math-card">
                    <h4>积分和求和</h4>
                    <div class="code-preview">
                      <pre><code>$$\int_0^1 x^2 dx = \frac{1}{3}$$

$$\sum_{n=1}^{\infty} \frac{1}{n^2} = \frac{\pi^2}{6}$$

$$\lim_{x \to \infty} \frac{1}{x} = 0$$</code></pre>
                    </div>
                    <div class="math-render" v-html="renderMarkdown('$$\\int_0^1 x^2 dx = \\frac{1}{3}$$\\n$$\\sum_{n=1}^{\\infty} \\frac{1}{n^2} = \\frac{\\pi^2}{6}$$\\n$$\\lim_{x \\to \\infty} \\frac{1}{x} = 0$$')"></div>
                  </div>

                  <div class="math-card">
                    <h4>矩阵</h4>
                    <div class="code-preview">
                      <pre><code>$$\begin{pmatrix}
1 & 0 \\
0 & 1
\end{pmatrix}$$

$$\begin{bmatrix}
a & b & c \\
d & e & f \\
g & h & i
\end{bmatrix}$$</code></pre>
                    </div>
                    <div class="math-render" v-html="renderMarkdown('$$\\begin{pmatrix}1 & 0 \\\\0 & 1\\end{pmatrix}$$\\n$$\\begin{bmatrix}a & b & c \\\\d & e & f \\\\g & h & i\\end{bmatrix}$$')"></div>
                  </div>
                </div>
              </div>

              <div class="math-category">
                <h3>物理公式</h3>
                <div class="math-card wide">
                  <h4>麦克斯韦方程组</h4>
                  <div class="code-preview">
                    <pre><code>$$\begin{aligned}
\nabla \times \vec{\mathbf{B}} -\, \frac1c\, \frac{\partial\vec{\mathbf{E}}}{\partial t} &= \frac{4\pi}{c}\vec{\mathbf{j}} \\
\nabla \cdot \vec{\mathbf{E}} &= 4 \pi \rho \\
\nabla \times \vec{\mathbf{E}}\, +\, \frac1c\, \frac{\partial\vec{\mathbf{B}}}{\partial t} &= \vec{\mathbf{0}} \\
\nabla \cdot \vec{\mathbf{B}} &= 0
\end{aligned}$$</code></pre>
                  </div>
                  <div class="math-render" v-html="renderMarkdown('$$\\begin{aligned}\\nabla \\times \\vec{\\mathbf{B}} -\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{E}}}{\\partial t} &= \\frac{4\\pi}{c}\\vec{\\mathbf{j}} \\\\\\nabla \\cdot \\vec{\\mathbf{E}} &= 4 \\pi \\rho \\\\\\nabla \\times \\vec{\\mathbf{E}}\\, +\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{B}}}{\\partial t} &= \\vec{\\mathbf{0}} \\\\\\nabla \\cdot \\vec{\\mathbf{B}} &= 0\\end{aligned}$$')"></div>
                </div>
              </div>
            </div>
          </section>

          <!-- 高级功能 -->
          <section id="advanced" class="feature-section">
            <h2 class="section-title">高级功能</h2>
            <div class="feature-grid">
              <div class="feature-card wide">
                <h3>数学公式 (LaTeX)</h3>
                <div class="code-preview">
                  <pre><code>行内公式：$E = mc^2$

块级公式：
$$
\int_{-\infty}^{\infty} e^{-x^2} dx = \sqrt{\pi}
$$

矩阵：
$$
\begin{pmatrix}
a & b \\
c & d
\end{pmatrix}
$$</code></pre>
                </div>
              </div>

              <div class="feature-card wide">
                <h3>表格</h3>
                <div class="code-preview">
                  <pre><code>| 功能 | 支持 | 备注 |
|------|------|------|
| 基础语法 | ✅ | 完全支持 |
| 代码高亮 | ✅ | 多语言支持 |
| LaTeX公式 | ✅ | KaTeX渲染 |</code></pre>
                </div>
              </div>

              <div class="feature-card wide">
                <h3>任务列表</h3>
                <div class="code-preview">
                  <pre><code>- [x] 已完成任务
- [ ] 待完成任务
- [x] 支持LaTeX公式
- [ ] 还有一个待完成任务</code></pre>
                </div>
              </div>
            </div>
          </section>

          <!-- 代码高亮 -->
          <section id="code" class="feature-section">
            <h2 class="section-title">代码高亮</h2>
            <div class="code-examples">
              <div class="code-example">
                <h4>JavaScript</h4>
                <div class="code-preview">
                  <pre><code>```javascript
function fibonacci(n) {
  if (n <= 1) return n;
  return fibonacci(n - 1) + fibonacci(n - 2);
}

console.log(fibonacci(10)); // 55
```</code></pre>
                </div>
              </div>

              <div class="code-example">
                <h4>Python</h4>
                <div class="code-preview">
                  <pre><code>```python
def quicksort(arr):
    if len(arr) <= 1:
        return arr
    
    pivot = arr[len(arr) // 2]
    left = [x for x in arr if x < pivot]
    middle = [x for x in arr if x == pivot]
    right = [x for x in arr if x > pivot]
    
    return quicksort(left) + middle + quicksort(right)
```</code></pre>
                </div>
              </div>

              <div class="code-example">
                <h4>CSS</h4>
                <div class="code-preview">
                  <pre><code>```css
.gradient-bg {
  background: linear-gradient(135deg, 
    #667eea 0%, 
    #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
}
```</code></pre>
                </div>
              </div>
            </div>
          </section>

          <!-- 交互功能 -->
          <section id="interactive" class="feature-section">
            <h2 class="section-title">交互功能</h2>
            <div class="interactive-demos">
              <div class="demo-card">
                <h3>实时预览</h3>
                <p>在左侧编辑器中输入内容，右侧会实时渲染预览效果</p>
                <div class="mini-editor">
                  <textarea 
                    v-model="liveDemo" 
                    placeholder="试试输入一些 Markdown..."
                    class="mini-textarea"
                  ></textarea>
                  <div class="mini-preview" v-html="renderMarkdown(liveDemo)"></div>
                </div>
              </div>

              <div class="demo-card">
                <h3>主题切换</h3>
                <p>支持多种代码高亮主题</p>
                <div class="theme-selector">
                  <button 
                    v-for="theme in themes" 
                    :key="theme.name"
                    @click="currentTheme = theme.name"
                    :class="{ active: currentTheme === theme.name }"
                    class="theme-btn"
                  >
                    {{ theme.label }}
                  </button>
                </div>
              </div>
            </div>
          </section>
        </div>
      </div>
    </main>

    <!-- 底部 -->
    <footer class="demo-footer">
      <div class="footer-content">
        <p>Powered by <strong>markdown-it</strong> • Built with <strong>Vue 3</strong></p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import MarkdownIt from 'markdown-it';
import hljs from 'highlight.js';
// @ts-ignore
import markdownItKatex from 'markdown-it-katex';

// 主题列表
const themes = [
  { name: 'default', label: '默认' },
  { name: 'github', label: 'GitHub' },
  { name: 'monokai', label: 'Monokai' },
  { name: 'tomorrow', label: 'Tomorrow' }
];

const currentTheme = ref('default');
const showEditor = ref(false);
const liveDemo = ref('**Hello** *World*!\n\n数学公式：$E = mc^2$\n\n$$\\int_0^1 x^2 dx = \\frac{1}{3}$$\n\n- Item 1\n- Item 2');

// 示例 Markdown 内容
const demoMarkdown = `# Markdown 功能展示

## 文本样式

这是一段普通文本，包含**粗体**、*斜体*、~~删除线~~和\`行内代码\`。

> 这是一个引用块，可以用来突出重要信息。

## 数学公式支持

### 行内公式
这是一个行内公式：$E = mc^2$，爱因斯坦的质能方程。

### 块级公式
$$
\\frac{d}{dx}\\left( \\int_{a}^{x} f(t) dt\\right) = f(x)
$$

更复杂的数学公式：

$$
\\begin{aligned}
\\nabla \\times \\vec{\\mathbf{B}} -\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{E}}}{\\partial t} &= \\frac{4\\pi}{c}\\vec{\\mathbf{j}} \\\\
\\nabla \\cdot \\vec{\\mathbf{E}} &= 4 \\pi \\rho \\\\
\\nabla \\times \\vec{\\mathbf{E}}\\, +\\, \\frac1c\\, \\frac{\\partial\\vec{\\mathbf{B}}}{\\partial t} &= \\vec{\\mathbf{0}} \\\\
\\nabla \\cdot \\vec{\\mathbf{B}} &= 0
\\end{aligned}
$$

### 矩阵和线性代数
$$
\\begin{pmatrix}
a & b \\\\
c & d
\\end{pmatrix}
\\begin{pmatrix}
x \\\\
y
\\end{pmatrix}
=
\\begin{pmatrix}
ax + by \\\\
cx + dy
\\end{pmatrix}
$$

### 积分和求和
$$
\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}
$$

$$
\\sum_{n=1}^{\\infty} \\frac{1}{n^2} = \\frac{\\pi^2}{6}
$$

## 列表

### 无序列表
- 第一项
- 第二项
  - 嵌套项 1
  - 嵌套项 2
- 第三项

### 有序列表
1. 首先
2. 然后
3. 最后

### 任务列表
- [x] 完成 Markdown 解析
- [x] 添加代码高亮
- [x] 添加 LaTeX 数学公式支持 ✨
- [ ] 优化移动端显示

## 代码块

### JavaScript
\`\`\`javascript
function greet(name) {
    return \`Hello, \${name}!\`;
}

const message = greet('World');
console.log(message);
\`\`\`

### Python 数学计算
\`\`\`python
import numpy as np
import matplotlib.pyplot as plt

# 创建数据
x = np.linspace(0, 2*np.pi, 100)
y = np.sin(x)

# 绘制图形
plt.plot(x, y, 'b-', label='sin(x)')
plt.xlabel('x')
plt.ylabel('y')
plt.title('正弦函数图像')
plt.legend()
plt.grid(True)
plt.show()
\`\`\`

### LaTeX 公式示例代码
\`\`\`latex
% 行内公式
$E = mc^2$

% 块级公式
$$
\\int_{-\\infty}^{\\infty} e^{-x^2} dx = \\sqrt{\\pi}
$$

% 矩阵
$$
\\begin{bmatrix}
1 & 0 & 0 \\\\
0 & 1 & 0 \\\\
0 & 0 & 1
\\end{bmatrix}
$$
\`\`\`

## 表格

| 特性 | 状态 | 说明 |
|------|------|------|
| 基础语法 | ✅ | 完全支持 |
| 代码高亮 | ✅ | 支持多种语言 |
| 表格 | ✅ | 支持对齐 |
| 任务列表 | ✅ | 交互式复选框 |
| LaTeX 公式 | ✅ | KaTeX 渲染 |
| 化学公式 | ✅ | 如 $\\ce{H2O}$ |

## 科学公式示例

### 物理公式
- 牛顿第二定律：$F = ma$
- 动能公式：$E_k = \\frac{1}{2}mv^2$
- 薛定谔方程：$i\\hbar\\frac{\\partial}{\\partial t}\\Psi = \\hat{H}\\Psi$

### 数学公式
- 欧拉公式：$e^{i\\pi} + 1 = 0$
- 二次公式：$x = \\frac{-b \\pm \\sqrt{b^2-4ac}}{2a}$
- 泰勒级数：$f(x) = \\sum_{n=0}^{\\infty} \\frac{f^{(n)}(a)}{n!}(x-a)^n$

### 统计学公式
正态分布概率密度函数：
$$
f(x) = \\frac{1}{\\sigma\\sqrt{2\\pi}} e^{-\\frac{1}{2}\\left(\\frac{x-\\mu}{\\sigma}\\right)^2}
$$

## 链接和图片

这是一个[链接示例](https://www.example.com)。

![示例图片](https://via.placeholder.com/300x200/4ecdc4/ffffff?text=Markdown+Demo)

## 分隔线

---

## 更多功能

这个 Markdown 渲染器还支持：

1. **自动链接检测**: https://www.example.com
2. **HTML 支持**: <kbd>Ctrl</kbd> + <kbd>C</kbd>
3. **表情符号**: :smile: :heart: :thumbsup:
4. **数学符号**: $\\alpha$, $\\beta$, $\\gamma$, $\\infty$, $\\sum$, $\\int$

> **提示**: 你可以在左侧编辑器中修改这些内容来测试实时预览功能！数学公式使用 \`$...$\` 表示行内公式，\`$$...$$\` 表示块级公式。
`;

const markdownContent = ref(demoMarkdown);

// 配置 markdown-it
const md: MarkdownIt = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string): string {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code class="hljs language-${lang}">` +
               hljs.highlight(str, { language: lang, ignoreIllegals: true }).value +
               '</code></pre>';
      } catch (__) {}
    }
    return `<pre class="hljs"><code class="hljs">` + md.utils.escapeHtml(str) + '</code></pre>';
  }
});

// 添加 KaTeX 插件支持 LaTeX 数学公式
md.use(markdownItKatex, {
  throwOnError: false,
  errorColor: '#cc0000',
  displayMode: false,
  fleqn: false,
  leqno: false,
  strict: false,
  trust: false,
  output: 'html',
  macros: {
    "\\f": "#1f(#2)"
  }
});

// 渲染 Markdown
const renderedMarkdown = computed(() => {
  return md.render(markdownContent.value);
});

const renderMarkdown = (content: string) => {
  return md.render(content);
};

// 方法
const toggleEditor = () => {
  showEditor.value = !showEditor.value;
};

const resetToDemo = () => {
  markdownContent.value = demoMarkdown;
};

const copyAllMarkdown = async () => {
  try {
    await navigator.clipboard.writeText(markdownContent.value);
    alert('Markdown 内容已复制到剪贴板！');
  } catch (err) {
    console.error('复制失败:', err);
  }
};

const exportHTML = () => {
  const html = renderedMarkdown.value;
  const blob = new Blob([html], { type: 'text/html' });
  const url = URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'markdown-export.html';
  a.click();
  URL.revokeObjectURL(url);
};

const scrollToSection = (sectionId: string) => {
  const element = document.getElementById(sectionId);
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' });
  }
};

onMounted(() => {
  // 加载代码高亮样式
  import('highlight.js/styles/default.css');
  // 加载 KaTeX 样式 - 确保正确加载
  import('katex/dist/katex.min.css').catch(() => {
    // 如果导入失败，尝试通过CDN加载
    const link = document.createElement('link');
    link.rel = 'stylesheet';
    link.href = 'https://cdn.jsdelivr.net/npm/katex@0.16.0/dist/katex.min.css';
    document.head.appendChild(link);
  });
});
</script>

<style scoped>
/* 全局样式 */
.markdown-demo-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部样式 */
.demo-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.highlight {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-menu a {
  color: #4a5568;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
  cursor: pointer;
}

.nav-menu a:hover {
  color: #667eea;
}

/* 主要内容 */
.demo-main {
  padding: 2rem 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
}

/* 介绍部分 */
.intro-section {
  text-align: center;
  margin-bottom: 3rem;
}

.intro-title {
  font-size: 3rem;
  font-weight: 800;
  color: #2d3748;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.intro-description {
  font-size: 1.2rem;
  color: #718096;
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.intro-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.primary-btn, .secondary-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.primary-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.secondary-btn {
  background: white;
  color: #4a5568;
  border: 2px solid #e2e8f0;
}

.secondary-btn:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

/* 编辑器和预览 */
.editor-preview-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
  margin-bottom: 3rem;
  transition: all 0.3s ease;
}

.editor-preview-container.show-editor {
  grid-template-columns: 1fr 1fr;
}

.editor-panel, .preview-panel {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  overflow: hidden;
}

.panel-header {
  background: #f7fafc;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-header h3 {
  margin: 0;
  color: #2d3748;
  font-weight: 600;
}

.reset-btn, .export-btn {
  padding: 0.5rem 1rem;
  background: #4299e1;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.reset-btn:hover, .export-btn:hover {
  background: #3182ce;
}

.markdown-editor {
  width: 100%;
  height: 500px;
  border: none;
  padding: 1.5rem;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  outline: none;
}

.markdown-preview {
  padding: 1.5rem;
  height: 500px;
  overflow-y: auto;
}

/* 数学公式展示 */
.math-examples {
  display: flex;
  flex-direction: column;
  gap: 3rem;
}

.math-category h3 {
  color: #2d3748;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.math-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
}

.math-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;
}

.math-card:hover {
  transform: translateY(-2px);
}

.math-card.wide {
  grid-column: 1 / -1;
}

.math-card h4 {
  color: #4a5568;
  margin-bottom: 1rem;
  font-weight: 600;
  font-size: 1rem;
}

.math-render {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1rem;
  margin-top: 1rem;
  border-left: 4px solid #667eea;
}

/* KaTeX 数学公式样式优化 */
.markdown-preview :deep(.katex-display),
.mini-preview :deep(.katex-display),
.math-render :deep(.katex-display) {
  margin: 1em 0;
  text-align: center;
  overflow-x: auto;
  overflow-y: hidden;
  padding: 0.5rem 0;
  display: block !important;
}

.markdown-preview :deep(.katex),
.mini-preview :deep(.katex),
.math-render :deep(.katex) {
  font-size: 1.1em;
  font-family: 'KaTeX_Main', 'Times New Roman', serif;
  line-height: 1.2;
}

.markdown-preview :deep(.katex-html),
.mini-preview :deep(.katex-html),
.math-render :deep(.katex-html) {
  display: inline-block;
  white-space: nowrap;
  vertical-align: baseline;
}

/* 修复行内公式的垂直对齐 */
.markdown-preview :deep(.katex:not(.katex-display)),
.mini-preview :deep(.katex:not(.katex-display)),
.math-render :deep(.katex:not(.katex-display)) {
  vertical-align: baseline;
  display: inline-block;
  line-height: 1;
}

/* 确保块级公式不会溢出 */
.markdown-preview :deep(.katex-display .katex),
.mini-preview :deep(.katex-display .katex),
.math-render :deep(.katex-display .katex) {
  display: block;
  margin: 0 auto;
  text-align: center;
}

/* 修复 aligned 环境的对齐问题 */
.markdown-preview :deep(.katex-display .katex .base),
.mini-preview :deep(.katex-display .katex .base),
.math-render :deep(.katex-display .katex .base) {
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 修复矩阵和对齐环境的样式 */
.markdown-preview :deep(.katex .arraycolsep),
.mini-preview :deep(.katex .arraycolsep),
.math-render :deep(.katex .arraycolsep) {
  width: 0.5em;
}

.markdown-preview :deep(.katex .mtable),
.mini-preview :deep(.katex .mtable),
.math-render :deep(.katex .mtable) {
  margin: 0 auto;
}

.markdown-preview :deep(.katex .col-align-c),
.mini-preview :deep(.katex .col-align-c),
.math-render :deep(.katex .col-align-c) {
  text-align: center;
}

.markdown-preview :deep(.katex .col-align-l),
.mini-preview :deep(.katex .col-align-l),
.math-render :deep(.katex .col-align-l) {
  text-align: left;
}

.markdown-preview :deep(.katex .col-align-r),
.mini-preview :deep(.katex .col-align-r),
.math-render :deep(.katex .col-align-r) {
  text-align: right;
}

/* 修复分数线的对齐 */
.markdown-preview :deep(.katex .frac-line),
.mini-preview :deep(.katex .frac-line),
.math-render :deep(.katex .frac-line) {
  border-bottom-width: 0.04em;
}

/* 响应式数学公式 */
@media (max-width: 768px) {
  .math-grid {
    grid-template-columns: 1fr;
  }
  
  .markdown-preview :deep(.katex-display),
  .mini-preview :deep(.katex-display),
  .math-render :deep(.katex-display) {
    overflow-x: auto;
    overflow-y: hidden;
    max-width: 100%;
    margin: 1rem 0;
    padding: 0.5rem;
    border-radius: 4px;
    background: rgba(255, 255, 255, 0.05);
  }
  
  .markdown-preview :deep(.katex),
  .mini-preview :deep(.katex),
  .math-render :deep(.katex) {
    font-size: 0.9em;
  }
  
  /* 移动端矩阵和表格的特殊处理 */
  .markdown-preview :deep(.katex .mtable),
  .mini-preview :deep(.katex .mtable),
  .math-render :deep(.katex .mtable) {
    font-size: 0.85em;
  }
}

/* 功能展示 */
.features-showcase {
  margin-top: 4rem;
}

.feature-section {
  margin-bottom: 4rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 2rem;
  text-align: center;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.feature-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-2px);
}

.feature-card.wide {
  grid-column: 1 / -1;
}

.feature-card h3 {
  color: #2d3748;
  margin-bottom: 1rem;
  font-weight: 600;
}

.code-preview {
  background: #f7fafc;
  border-radius: 8px;
  overflow: hidden;
}

.code-preview pre {
  margin: 0;
  padding: 1rem;
  font-size: 0.875rem;
  line-height: 1.5;
  color: #4a5568;
}

.code-examples, .code-example {
  margin-bottom: 2rem;
}

.code-example h4 {
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

/* 交互功能 */
.interactive-demos {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.demo-card {
  background: white;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.demo-card h3 {
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.demo-card p {
  color: #718096;
  margin-bottom: 1.5rem;
}

.mini-editor {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  height: 200px;
}

.mini-textarea {
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  resize: none;
  outline: none;
}

.mini-textarea:focus {
  border-color: #667eea;
}

.mini-preview {
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  padding: 1rem;
  overflow-y: auto;
  background: #fafafa;
  font-size: 12px;
}

.theme-selector {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.theme-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.875rem;
}

.theme-btn:hover {
  border-color: #cbd5e0;
}

.theme-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 底部 */
.demo-footer {
  background: #2d3748;
  color: white;
  text-align: center;
  padding: 2rem;
  margin-top: 4rem;
}

.footer-content p {
  margin: 0;
  color: #a0aec0;
}

/* 响应式 */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }

  .nav-menu {
    gap: 1rem;
  }

  .intro-title {
    font-size: 2rem;
  }

  .editor-preview-container.show-editor {
    grid-template-columns: 1fr;
  }

  .mini-editor {
    grid-template-columns: 1fr;
    height: auto;
    gap: 1rem;
  }

  .mini-textarea {
    height: 100px;
  }

  .mini-preview {
    height: 100px;
  }

  .interactive-demos {
    grid-template-columns: 1fr;
  }
}

/* Markdown 内容样式 */
.markdown-preview :deep(h1),
.mini-preview :deep(h1) {
  color: #2d3748;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 0.5rem;
}

.markdown-preview :deep(h2),
.mini-preview :deep(h2) {
  color: #4a5568;
  margin-top: 2rem;
}

.markdown-preview :deep(code),
.mini-preview :deep(code) {
  background: #f7fafc;
  color: #e53e3e;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-size: 0.875em;
}

.markdown-preview :deep(blockquote),
.mini-preview :deep(blockquote) {
  border-left: 4px solid #667eea;
  margin: 1rem 0;
  padding-left: 1rem;
  color: #718096;
  background: #f7fafc;
  padding: 1rem;
  border-radius: 0 8px 8px 0;
}

.markdown-preview :deep(table),
.mini-preview :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1rem 0;
}

.markdown-preview :deep(th),
.markdown-preview :deep(td),
.mini-preview :deep(th),
.mini-preview :deep(td) {
  border: 1px solid #e2e8f0;
  padding: 0.75rem;
  text-align: left;
}

.markdown-preview :deep(th),
.mini-preview :deep(th) {
  background: #f7fafc;
  font-weight: 600;
}

.markdown-preview :deep(img),
.mini-preview :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
}

.markdown-preview :deep(.hljs),
.mini-preview :deep(.hljs) {
  background: #f8f9fa !important;
  border-radius: 8px;
  padding: 1rem !important;
  margin: 1rem 0;
  font-size: 0.875rem;
  line-height: 1.5;
}
</style>
