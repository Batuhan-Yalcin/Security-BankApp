import { createGlobalStyle } from 'styled-components';

// NOT: @import kullanımından kaçınıyoruz - fontlar index.html'de tanımlanmalı
// Örneğin: <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
const GlobalStyles = createGlobalStyle`
  * {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
  }

  body {
    font-family: 'Poppins', 'Roboto', 'Helvetica', 'Arial', sans-serif;
    background-color: #F5F9FF;
    color: #1D3557;
    overflow-x: hidden;
  }

  a {
    text-decoration: none;
    color: inherit;
  }

  button {
    cursor: pointer;
    border: none;
    outline: none;
  }

  input, textarea, select {
    font-family: 'Poppins', 'Roboto', 'Helvetica', 'Arial', sans-serif;
  }

  /* Animasyon sınıfları */
  .fade-in {
    animation: fadeIn 0.5s ease-in-out;
  }

  .slide-up {
    animation: slideUp 0.5s ease-in-out;
  }

  .slide-left {
    animation: slideLeft 0.5s ease-in-out;
  }

  @keyframes fadeIn {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }

  @keyframes slideUp {
    from {
      transform: translateY(20px);
      opacity: 0;
    }
    to {
      transform: translateY(0);
      opacity: 1;
    }
  }

  @keyframes slideLeft {
    from {
      transform: translateX(20px);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }

  /* Scroll bar stilini özelleştirme */
  ::-webkit-scrollbar {
    width: 8px;
  }

  ::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 10px;
  }

  ::-webkit-scrollbar-thumb {
    background: #1D3557;
    border-radius: 10px;
  }

  ::-webkit-scrollbar-thumb:hover {
    background: #457B9D;
  }
`;

export default GlobalStyles; 