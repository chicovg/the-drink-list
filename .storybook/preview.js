import '../public/css/site.css';
import '@yaireo/tagify/dist/tagify.css';
import './overrides.css';

export const parameters = {
  actions: { argTypesRegex: "^on[A-Z].*" },
  controls: {
    matchers: {
      color: /(background|color)$/i,
      date: /Date$/,
    },
  },
}
