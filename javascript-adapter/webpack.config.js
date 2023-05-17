import path from 'path'
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default {
    entry: './index.js',
    mode: 'production',
    module: {
        rules: [
            {
                test: /\.js$/,
                exclude: /(node_modules|bower_components)/,
            }]
    },
    experiments: {
        outputModule: true
    },
    output: {
        libraryTarget: 'module',
        filename: 'index.js',
        path: path.resolve(__dirname, 'dist')
    }
};