# KeyStrokeMidiPlayer
このツールはMidiファイル(`.mid`)を読み込み, 定義された`NOTE_ON`を`WM_KEYDOWN`へ, `NOTE_OFF`を`WM_KEYUP`へ変換しキー入力として出力することが出来るツールです.
複数トラックが収録されているmidiファイルには対応していません. (出力されますが, 正確な動作は期待できません.)

## Configについて
### generalconfig.json

| キー                        |    型    | 幅     |  初期値  | 説明                                                             |
|---------------------------|:-------:|-------|:-----:|:---------------------------------------------------------------|
| debug                     | boolean |       | false |                                                                |
| WindowName                | String  |       |       | キー入力先となるウィンドウの名前                                               |
| OutOfRangeCopyNearestNote | boolean |       | true  | 定義されていない音階を再生しようとした際に定義されている値のうち一番近いものを代わりに使用するかどうか            |
| forceUsingVKCode          | boolean |       | false | `config.json`にて直接vkCodeを記述するかどうか                               |
| NoteMaxNumber             |   int   | 0~127 |       | `config.json`にて定義する音階値のうち一番低いもの                                |
| NoteMinNumber             |   int   | 0~127 |       | `config.json`にて定義する音階値のうち一番高いもの                                |
| NoteNumberOffset          |   int   |       |  15   | `NoteMaxNumber`と`NoteMinNumber`にこの値を加算する.<br/> 定義後の調整に使用するとよい. |

### config.json
| キー                          | 値                                                                                                                                                        |
|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Midiメッセージにおける音階を表す数値(`int`) | 入力するキーの文字列表現(`String`)<br/>`forceUsingVKCode`が`true`ならキーに対応した[vkCode](https://learn.microsoft.com/ja-jp/windows/win32/inputdev/virtual-key-codes)(`int`) |
