package myswing.playmusic;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import stu.FileUtils;
import utils.utils.JsonUtils;
import utils.utils.RandomUtils;

/**
 * 播放音乐
 * 1.打开目录选择一首歌并添加到播放列表 并开始播放 并将同目录下的文件都加到播放列表中 单独添加音乐
 * 2.播放 暂停开始结束 快进后退 音量变化显示 音乐总时间剩余时长显示
 * 3.音乐总条数显示
 */
public class AudioPlayerApp extends Application {
	private static final String filePath = System.getProperty("user.dir") + "/use/list.txt";
	public static final String errorPath = System.getProperty("user.dir") + "/use/error.log";
	private MediaPlayer mediaPlayer;
	private Media media;
	private File selectedFile;
	private boolean isPlaying = false;
	private int currentTrackIndex = 0;
	private Slider progressBar;
	private Slider volumeSlider;
	private Button playButton;//播放暂停按钮
	private Label totalSongsLabel;
	private Label currSong;
	private Label timeLabel;
	private Label volumeLabel;
	private ListView<String> listView;
	private final ObservableList<String> playlist = FXCollections.observableArrayList();
	private final Map<String, File> fileMap = new HashMap<>();
	private PlaybackMode playbackMode = PlaybackMode.SEQUENTIAL;  // 默认顺序播放

	@Override
	public void start(Stage primaryStage) {
		// 创建主窗口
		primaryStage.setTitle("JavaFX 音频播放器");
		primaryStage.setMinWidth(600);
		primaryStage.setMinHeight(450);

		// 创建控件
		Button openButton = new Button("打开文件");
		Button addSongButton = new Button("添加歌曲");
		playButton = new Button("播放/暂停");
		Button stopButton = new Button("停止");
		Button forwardButton = new Button("快进");
		Button backwardButton = new Button("回退");
		Button clearPlayList = new Button("清空播放列表");
		Button save = new Button("保存列表");

		volumeSlider = new Slider(0, 100, 50);  // 音量滑块
		volumeLabel = new Label("音量:50% ");
		HBox volumeBox = new HBox(10, volumeLabel, volumeSlider);
		volumeBox.setAlignment(Pos.CENTER_LEFT);  // 确保音量滑块居中对齐

		progressBar = new Slider(0, 100, 0);  // 进度条
		progressBar.setPrefWidth(200);

		// 添加总时间和剩余时间显示
		timeLabel = new Label("00:00 | 00:00");
		HBox timeBox = new HBox(10, timeLabel);
		currSong = new Label("当前播放: 无");
		HBox currBox = new HBox(10, currSong);

		// 播放模式选择
		ComboBox<PlaybackMode> playbackModeComboBox = new ComboBox<>();
		playbackModeComboBox.getItems().addAll(PlaybackMode.values());
		playbackModeComboBox.setValue(playbackMode);
		playbackModeComboBox.setOnAction(e -> playbackMode = playbackModeComboBox.getValue());

		// 显示音乐总数
		totalSongsLabel = new Label("目录下共有 0 首音乐");
		readPlayList();
		// 设置按钮监听器
		openButton.setOnAction(e -> openFile());
		addSongButton.setOnAction(e -> addSingleSong());
		playButton.setOnAction(e -> togglePlayPause());
		stopButton.setOnAction(e -> stopPlayback());
		forwardButton.setOnAction(e -> seekForward());
		backwardButton.setOnAction(e -> seekBackward());
		clearPlayList.setOnAction(e -> clearPlayList());

		save.setOnAction(e -> saveList());

		//设置进度条监听器 滑块监听器
		addBarSliderListener();

		// 创建布局
		VBox controlBox = new VBox(10, openButton, addSongButton, playButton, stopButton, forwardButton, backwardButton, clearPlayList, save, volumeBox, playbackModeComboBox, timeBox, currBox);
		controlBox.setPadding(new Insets(10));

		// 播放列表
		listView = new ListView<>(playlist);
		listView.setPrefWidth(200);
		//添加播放列表按钮
		addPlayListButton(listView);

		HBox mainLayout = new HBox(10, controlBox, progressBar, listView);
		mainLayout.setPadding(new Insets(10));

		// 底部显示音乐总数
		HBox bottomLayout = new HBox(totalSongsLabel);
		bottomLayout.setPadding(new Insets(10));

		// 将主布局和底部布局组合在一起
		VBox fullLayout = new VBox(mainLayout, bottomLayout);
		fullLayout.setPadding(new Insets(10));

		// 创建场景并设置到舞台
		Scene scene = new Scene(fullLayout);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 添加播放列表按钮
	 */
	private void addPlayListButton(ListView<String> listView) {
		// 添加右键菜单以删除歌曲
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem = new MenuItem("播放");
		menuItem.setOnAction(e -> {
			String selectedItem = listView.getSelectionModel().getSelectedItem();
			loadMedia(fileMap.get(selectedItem));
		});
		contextMenu.getItems().add(menuItem);


		menuItem = new MenuItem("删除");
		menuItem.setOnAction(e -> {
			String selectedItem = listView.getSelectionModel().getSelectedItem();
			if (selectedItem != null) {
				playlist.remove(selectedItem);
				fileMap.remove(selectedItem);
				updateTotalSongsLabel();
				if (mediaPlayer != null && media.getSource().equals(selectedItem)) {
					stopPlayback();
				}
			}
		});
		contextMenu.getItems().add(menuItem);


		listView.setContextMenu(contextMenu);
	}

	/**
	 * 设置进度条监听器 滑块监听器
	 */
	private void addBarSliderListener() {
		// 设置进度条监听器
		progressBar.valueProperty().addListener((obs, oldValue, newValue) -> {
			updateTimes();
			if (mediaPlayer != null && progressBar.isValueChanging()) {
				mediaPlayer.seek(mediaPlayer.getMedia().getDuration().multiply(newValue.doubleValue() / 100));
			}
		});

		// 设置音量滑块监听器
		volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
			String value = "音量: " + String.format("%3d", (int) (volumeSlider.getValue())) + "%";
			volumeLabel.setText(value);
			if (mediaPlayer != null) {
				mediaPlayer.setVolume(newValue.doubleValue() / 100);
			}
		});
	}

	// 打开文件选择器并加载音频文件
	private void openFile() {
		FileChooser fChooser = new FileChooser();
		fChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("音频文件", "*.mp3", "*.wav", "*.aac", "*.m4a"));
		selectedFile = fChooser.showOpenDialog(null);

		if (selectedFile != null) {
			// 加载同一目录下的所有音频文件
			loadPlaylistFromDirectory(selectedFile.getParentFile());

			// 更新音乐总数标签
			updateTotalSongsLabel();

			// 加载选中的文件第一个文件
			loadMedia(selectedFile);
		}
	}

	// 添加单个歌曲到播放列表
	private void addSingleSong() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("音频文件", "*.mp3", "*.wav", "*.aac", "*.m4a", "*.flac"));
		selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			if (!playlist.contains(selectedFile.getAbsolutePath())) {
				playlist.add(selectedFile.getName());
				fileMap.put(selectedFile.getName(), selectedFile);
				updateTotalSongsLabel();
			}
			if (!isPlaying) {
				loadMedia(selectedFile);
			}
		}
	}

	// 加载同一目录下的所有音频文件
	private void loadPlaylistFromDirectory(File directory) {
		if (directory != null && directory.isDirectory()) {
			List<File> audioFiles = new ArrayList<>();
			File newFile;
			for (File file : Objects.requireNonNull(directory.listFiles())) {
				if (file.isFile()) {
					try {
						newFile = isSupportedAudioFile(file);
						if (newFile == null) {
							System.out.println("isSupportedAudioFile error " + file.toURI().toString());
							continue;
						}
						audioFiles.add(newFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			audioFiles.sort(Comparator.comparing(File::getName));  // 按文件名排序
			for (File file : audioFiles) {
				if (!playlist.contains(file.getName())) {
					playlist.add(file.getName());
					fileMap.put(file.getName(), file);
				}
			}
			updateTotalSongsLabel();
		}
	}

	// 检查文件是否为支持的音频格式
	private File isSupportedAudioFile(File file) throws IOException {
		String extension = getFileExtension(file);
		if (extension == null) {
			return null;
		}
		extension = extension.toLowerCase();
		switch (extension) {
			case "mp3":
			case "wav":
			case "aac":
			case "m4a":
				return file;
		}
		return null;
	}

	// 获取文件扩展名
	private String getFileExtension(File file) {
		String fileName = file.getName();
		int lastIndexOfDot = fileName.lastIndexOf('.');
		if (lastIndexOfDot == -1) {
			return null;
		}
		return fileName.substring(lastIndexOfDot + 1);
	}


	private String getFilePath(File file) {
		try {
			File newFile = isSupportedAudioFile(file);
			if (newFile != null) {
				return file.toURI().toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("isSupportedAudioFile error " + file.toURI().toString());
		return null;
	}

	// 加载媒体文件(加载就播放 手动关闭)
	private void loadMedia(File file) {
		try {
			String filePath = getFilePath(file);
			if (filePath == null) {
				return;
			}
			long start = System.currentTimeMillis();
			if (isPlaying) {
				stopPlayback();
				if (media != null && media.getSource().equals(filePath)) {
					return;
				}
			}
			media = new Media(filePath);
			mediaPlayer = new MediaPlayer(media);

			// 设置默认音量
			mediaPlayer.setVolume(volumeSlider.getValue() / 100);

			// 更新进度条的最大值
			media.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
				if ("duration".equals(change.getKey())) {
					double duration = media.getDuration().toSeconds();
					progressBar.setMax(duration);
					updateTimes();
				}
			});

			// 监听播放进度
			mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
				if (!progressBar.isValueChanging()) {
					updateTimes();
					if (media != null) {
						double progress = (newTime.toSeconds() / media.getDuration().toSeconds()) * 100;
						progressBar.setValue(progress);
					} else {
						progressBar.setValue(0);
					}
				}
			});
			System.out.println("loadMedia " + file.getAbsolutePath() + " 耗时" + (System.currentTimeMillis() - start) + " ms");
			startPlay();
			updateCurrPlayer(file.getName());
			mediaPlayer.setOnError(() -> System.err.println("播放错误: " + mediaPlayer.getError()));
			// 当播放结束时自动播放下一首
			mediaPlayer.setOnEndOfMedia(this::endPlay);
		} catch (Exception e) {
			FileUtils.writeFileAppendsUtf8(errorPath, e.toString() + "\n", true);
			e.printStackTrace();
		}
	}

	// 切换播放/暂停
	private void togglePlayPause() {
		if (mediaPlayer != null) {
			if (isPlaying) {
				mediaPlayer.pause();
				playButton.setText("播放");
			} else {
				mediaPlayer.play();
				playButton.setText("暂停");
			}
			isPlaying = !isPlaying;
		}
	}

	//开始播放
	private void startPlay() {
		mediaPlayer.play();
		playButton.setText("暂停");
		isPlaying = true;
	}

	// 停止播放
	private void stopPlayback() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			isPlaying = false;
			playButton.setText("播放");
			progressBar.setValue(0);
		} else {
			System.out.println("stopPlayback error mediaPlayer null");
		}
	}

	// 快进 5 秒
	private void seekForward() {
		if (mediaPlayer != null) {
			double currentTime = mediaPlayer.getCurrentTime().toSeconds();
			double newTime = Math.min(currentTime + 5, media.getDuration().toSeconds());
			mediaPlayer.seek(Duration.seconds(newTime));
		}
	}

	// 回退 5 秒
	private void seekBackward() {
		if (mediaPlayer != null) {
			double currentTime = mediaPlayer.getCurrentTime().toSeconds();
			double newTime = Math.max(currentTime - 5, 0);
			mediaPlayer.seek(Duration.seconds(newTime));
		}
	}

	// 播放下一首
	private void playNextTrack() {
		if (playlist.isEmpty()) {
			return;
		}

		switch (playbackMode) {
			case LOOP_ALL:
				currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
				break;
			case RANDOM:
				currentTrackIndex = RandomUtils.random(0, playlist.size() - 1);
				break;
			case SEQUENTIAL:
				if (currentTrackIndex + 1 < playlist.size()) {
					currentTrackIndex = (currentTrackIndex + 1) % playlist.size();
				} else {
					return;
				}
				break;
			default:
				break;
		}
		loadMedia(fileMap.get(playlist.get(currentTrackIndex)));
	}

	/**
	 * 清空播放列表
	 */
	private void clearPlayList() {
		playlist.clear();
		fileMap.clear();
		stopPlayback();
		updateTotalSongsLabel();
		mediaPlayer = null;
		media = null;
	}

	/**
	 * 保存列表
	 */
	private void saveList() {
		Map<String, String> map = new HashMap<>();
		for (Map.Entry<String, File> entry : fileMap.entrySet()) {
			if (!map.containsKey(entry.getKey())) {
				map.put(entry.getKey(), entry.getValue().getAbsolutePath());
			}
		}

		FileUtils.writeFileAppendsUtf8(filePath, JsonUtils.writeValue(map), false);
	}

	// 更新音乐总数标签
	private void updateTotalSongsLabel() {
		totalSongsLabel.setText("目录下共有 " + playlist.size() + " 首音乐");
	}

	/**
	 * 更新视频媒体时间
	 */
	private void updateTimes() {
		if (isPlaying && media != null) {
			Duration totalDuration = media.getDuration();
			Duration currentTime = mediaPlayer.getCurrentTime();
			Duration remainingTime = totalDuration.subtract(currentTime);
			// 更新总时长和剩余时间显示
			timeLabel.setText(formatTime(totalDuration.toSeconds()) + " | -" + formatTime(remainingTime.toSeconds()));
		} else {
			timeLabel.setText("00:00 | 00:00");
		}
	}

	/**
	 * 更新当前播放
	 */
	private void updateCurrPlayer(String fileName) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n"); // 添加换行符
		for (int i = 0; i < fileName.length(); i += 20) {
			sb.append(fileName, i, Math.min(i + 20, fileName.length()));
			sb.append("\n"); // 添加换行符
		}
		currSong.setText("当前播放: " + sb.toString());
		listView.scrollTo(fileName);
		listView.getSelectionModel().select(fileName);
	}


	/**
	 * 格式化时间
	 *
	 * @param seconds 180s -> 03:00
	 * @return 格式化的时间
	 */
	private static String formatTime(double seconds) {
		int minute = (int) (seconds / 60);
		return String.format("%02d:%02d", minute, (int) (seconds - minute * 60));
	}

	/**
	 * 读播放列表
	 */
	private void readPlayList() {
		String readFileAppend = FileUtils.readFileAppend(filePath);
		Map<String, String> map = JsonUtils.readValue(readFileAppend, Map.class);
		if (map == null || map.isEmpty()) {
			return;
		}
		File file;
		File first = null;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			try {
				file = new File(entry.getValue());
				if (first == null) {
					first = file;
				}
				fileMap.put(entry.getKey(), file);
				playlist.add(entry.getKey());
			} catch (Exception e) {
				FileUtils.writeFileAppendsUtf8(errorPath, e.toString(), true);
				e.printStackTrace();
			}
		}
		updateTotalSongsLabel();
	}

	/**
	 * 删除临时转换文件
	 */
	private void removeTempTransFile(File file) {
		// 删除临时文件
		if (file != null && file.exists()) {
			try {
				Files.delete(file.toPath());
				System.out.println("临时文件已删除: " + file.getAbsolutePath());
			} catch (IOException e) {
				System.err.println("文件删除失败: " + e.getMessage());
			}
		}
	}


	/**
	 * 结束播放
	 */
	private void endPlay() {
		stopPlayback();
		removeTempTransFile(selectedFile);
		playNextTrack();
	}


	// 播放模式枚举
	public enum PlaybackMode {
		SEQUENTIAL("顺序播放"),
		RANDOM("随机播放"),
		LOOP_ALL("循环播放"),
		LOOP_SINGLE("单曲循环");

		private final String description;

		PlaybackMode(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			return description;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}