package algorithm;

/**
 * 迪杰斯特拉算法介绍
 * <p>
 * 迪杰斯特拉(Dijkstra)算法是典型最短路径算法，用于计算一个节点到其他节点的最短路径。
 * 它的主要特点是以起始点为中心向外层层扩展(广度优先搜索思想)，直到扩展到终点为止。
 * <p>
 * <p>
 * 基本思想
 * <p>
 * 通过Dijkstra计算图G中的最短路径时，需要指定起点s(即从顶点s开始计算)。
 * <p>
 * 此外，引进两个集合S和U。S的作用是记录已求出最短路径的顶点(以及相应的最短路径长度)，而U则是记录还未求出最短路径的顶点(以及该顶点到起点s的距离)。
 * <p>
 * 初始时，S中只有起点s；U中是除s之外的顶点，并且U中顶点的路径是"起点s到该顶点的路径"。然后，从U中找出路径最短的顶点，并将其加入到S中；接着，更新U中的顶点和顶点对应的路径。 然后，再从U中找出路径最短的顶点，并将其加入到S中；接着，更新U中的顶点和顶点对应的路径。 ... 重复该操作，直到遍历完所有顶点。
 * <p>
 * <p>
 * 操作步骤
 * <p>
 * (1) 初始时，S只包含起点s；U包含除s外的其他顶点，且U中顶点的距离为"起点s到该顶点的距离"[例如，U中顶点v的距离为(s,v)的长度，然后s和v不相邻，则v的距离为∞]。
 * <p>
 * (2) 从U中选出"距离最短的顶点k"，并将顶点k加入到S中；同时，从U中移除顶点k。
 * <p>
 * (3) 更新U中各个顶点到起点s的距离。之所以更新U中顶点的距离，是由于上一步中确定了k是求出最短路径的顶点，从而可以利用k来更新其它顶点的距离；例如，(s,v)的距离可能大于(s,k)+(k,v)的距离。
 * <p>
 * (4) 重复步骤(2)和(3)，直到遍历完所有顶点。
 * <p>
 * 单纯的看上面的理论可能比较难以理解，下面通过实例来对该算法进行说明。
 */

/**
 * 迪杰斯特拉算法图解
 *
 */
public class MatrixUDG {

	private int mEdgNum;        // 边的数量
	private char[] mVexs;       // 顶点集合
	private int[][] mMatrix;    // 邻接矩阵
	private static final int INF = Integer.MAX_VALUE;   // 最大值

	/*
	 * Dijkstra最短路径。
	 * 即，统计图中"顶点vs"到其它各个顶点的最短路径。
	 *
	 * 参数说明：
	 *       vs -- 起始顶点(start vertex)。即计算"顶点vs"到其它顶点的最短路径。
	 *     prev -- 前驱顶点数组。即，prev[i]的值是"顶点vs"到"顶点i"的最短路径所经历的全部顶点中，位于"顶点i"之前的那个顶点。
	 *     dist -- 长度数组。即，dist[i]是"顶点vs"到"顶点i"的最短路径的长度。
	 */
	public void dijkstra(int vs, int[] prev, int[] dist) {
		// flag[i]=true表示"顶点vs"到"顶点i"的最短路径已成功获取
		boolean[] flag = new boolean[mVexs.length];

		// 初始化
		for (int i = 0; i < mVexs.length; i++) {
			flag[i] = false;          // 顶点i的最短路径还没获取到。
			prev[i] = 0;              // 顶点i的前驱顶点为0。
			dist[i] = mMatrix[vs][i];  // 顶点i的最短路径为"顶点vs"到"顶点i"的权。
		}

		// 对"顶点vs"自身进行初始化
		flag[vs] = true;
		dist[vs] = 0;

		// 遍历mVexs.length-1次；每次找出一个顶点的最短路径。
		int k = 0;
		for (int i = 1; i < mVexs.length; i++) {
			// 寻找当前最小的路径；
			// 即，在未获取最短路径的顶点中，找到离vs最近的顶点(k)。
			int min = INF;
			for (int j = 0; j < mVexs.length; j++) {
				if (!flag[j] && dist[j] < min) {
					min = dist[j];
					k = j;
				}
			}
			// 标记"顶点k"为已经获取到最短路径
			flag[k] = true;

			// 修正当前最短路径和前驱顶点
			// 即，当已经"顶点k的最短路径"之后，更新"未获取最短路径的顶点的最短路径和前驱顶点"。
			for (int j = 0; j < mVexs.length; j++) {
				int tmp = (mMatrix[k][j] == INF ? INF : (min + mMatrix[k][j]));
				if (!flag[j] && (tmp < dist[j])) {
					dist[j] = tmp;
					prev[j] = k;
				}
			}
		}

		// 打印dijkstra最短路径的结果
		System.out.printf("dijkstra(%c): \n", mVexs[vs]);
		for (int i = 0; i < mVexs.length; i++)
			System.out.printf("  shortest(%c, %c)=%d\n", mVexs[vs], mVexs[i], dist[i]);
	}
}