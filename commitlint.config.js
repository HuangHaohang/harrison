/* eslint-disable @typescript-eslint/no-var-requires */

// 定义你的项目模块，对应提交时的 scope
const packages = [
  'ui',
  'ai',
  'gateway',
  'sso'
]

const scopes = [
  'project',
  'core',
  'style',
  'docs',
  'ci',
  'dev',
  'build',
  'deploy',
  'other',
  'typography',
  'color',
  'border',
  'var',
]

module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'scope-enum': [
      2,
      'always',
      [...packages, ...scopes],
    ],
    'body-leading-blank': [1, 'always'],
    'footer-leading-blank': [1, 'always'],
    'header-max-length': [2, 'always', 72],
    'scope-case': [2, 'always', 'lower-case'],
    'subject-case': [
      1,
      'never',
      ['sentence-case', 'start-case', 'pascal-case', 'upper-case'],
    ],
    'subject-empty': [2, 'never'],
    'subject-full-stop': [2, 'never', '.'],
    'type-case': [2, 'always', 'lower-case'],
    'type-empty': [2, 'never'],
    'type-enum': [
      2,
      'always',
      [
        'build',
        'chore',
        'ci',
        'docs',
        'feat',
        'fix',
        'perf',
        'refactor',
        'revert',
        'release',
        'style',
        'test',
        'improvement',
      ],
    ],
  },
}