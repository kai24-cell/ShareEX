# ShareEX
ShareEX(Share Experience)
企業向けでなく、同級生や自分向けのポートフォリオ管理ツール。これを突き詰めれば、部下の育成の効率化を狙える。
学習や開発の試行錯誤をテキスト、画像、タグ付きで投稿して後から検索・再発見できるサービス。
同級生や自分自身が過去の学習ログを振り返り、次の学習へ活かすことができる。
いづれは企業向けとしてサービスを展開して、上司が部下の育成を効率的にするためのツールへ発展させたいと考えてる。

開発環境
Java version 17

機能
投稿機能
　テキスト投稿
　画像添付
　タグ付け
検索
　タグ検索
　フリーワード検索
　関連投稿表示
管理・制約
　公開範囲設定、投稿編集・削除・履歴管理
　招待制運用
ブックマーク
　重要な投稿を個人でブックマーク可能
　後で振り返りやすくする
DemoApplication.java:実行
application.propertied:Java側から特定のSQLへの指定先を指してる
Post.java:postテーブルとjavaを繋ぐ(投稿内容,画像のurl,主キーの生成,中間テーブルを介したTagとの紐づけ,日時)
Tag.java:重複したタグの設定不可,中間テーブルの権限を放棄(Post.javaが持ってる)
ApiController.java:requestとfile(画像)をpostServiceへ送るコントローラ
PostService.java:Tagが重複してたらDB内のそれを使って、なかったら新しくつくるって処理を書いたサービス
PostRepository.java,TagRepository:リポジトリ